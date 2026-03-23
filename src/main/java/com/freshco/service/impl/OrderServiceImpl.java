package com.freshco.service.impl;

import com.freshco.dto.request.PlaceOrderRequestDto;
import com.freshco.dto.response.OrderCountResponseDto;
import com.freshco.dto.response.OrderItemResponseDto;
import com.freshco.dto.response.OrderResponseDto;
import com.freshco.dto.response.PagedResponseDto;
import com.freshco.entity.*;
import com.freshco.enums.OrderStatus;
import com.freshco.enums.PaymentMethod;
import com.freshco.enums.PaymentStatus;
import com.freshco.exception.BadRequestException;
import com.freshco.exception.ResourceNotFoundException;
import com.freshco.repository.*;
import com.freshco.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final CartRepository cartRepository;

    private final AddressRepository addressRepository;

    private final ProductRepository productRepository;

    private final ShopRepository shopRepository;

    @Override
    @Transactional
    public OrderResponseDto placeOrder(PlaceOrderRequestDto request, Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("Your cart is empty"));

        if (cart.getCartItems().isEmpty()) {
            throw new BadRequestException("Your cart is empty");
        }

        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", request.getAddressId()));

        if (!address.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You can only use your own address");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();

            if (product.getQuantity() < cartItem.getQuantity()) {
                throw new BadRequestException("Not enough stock for " + product.getName()
                        + ". Available: " + product.getQuantity());
            }

            BigDecimal unitPrice = product.getPrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .unitPrice(unitPrice)
                    .build();

            orderItems.add(orderItem);
        }

        Order order = Order.builder()
                .customer(cart.getUser())
                .shop(cart.getShop())
                .address(address)
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .paymentMethod(PaymentMethod.CASH_ON_DELIVERY)
                .orderItems(orderItems)
                .build();

        orderItems.forEach(item -> item.setOrder(order));

        Order savedOrder = orderRepository.save(order);

        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }

        cart.getCartItems().clear();
        cart.setShop(null);
        cartRepository.save(cart);

        return mapToOrderResponseDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        boolean isCustomer = order.getCustomer().getId().equals(userId);
        boolean isSeller = order.getShop().getOwner().getId().equals(userId);

        if (!isCustomer && !isSeller) {
            throw new AccessDeniedException("You don't have access to this order");
        }

        return mapToOrderResponseDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDto<OrderResponseDto> getMyOrders(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Order> orderPage = orderRepository.findByCustomerIdOrderByCreatedAtDesc(userId, pageable);

        List<OrderResponseDto> content = orderPage.getContent().stream()
                .map(this::mapToOrderResponseDto)
                .toList();

        return PagedResponseDto.<OrderResponseDto>builder()
                .content(content)
                .page(orderPage.getNumber())
                .size(orderPage.getSize())
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .last(orderPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponseDto<OrderResponseDto> getShopOrders(Long shopId, Long sellerId, int page, int size) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new ResourceNotFoundException("Shop", "id", shopId));

        if (!shop.getOwner().getId().equals(sellerId)) {
            throw new AccessDeniedException("You can only view orders for your own shop");
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<Order> orderPage = orderRepository.findByShopIdOrderByCreatedAtDesc(shopId, pageable);

        List<OrderResponseDto> content = orderPage.getContent().stream()
                .map(this::mapToOrderResponseDto)
                .toList();

        return PagedResponseDto.<OrderResponseDto>builder()
                .content(content)
                .page(orderPage.getNumber())
                .size(orderPage.getSize())
                .totalElements(orderPage.getTotalElements())
                .totalPages(orderPage.getTotalPages())
                .last(orderPage.isLast())
                .build();
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrderStatus(Long orderId, String status, Long sellerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (!order.getShop().getOwner().getId().equals(sellerId)) {
            throw new AccessDeniedException("You can only update order from your own shop");
        }

        OrderStatus newStatus = parseOrderStatus(status);

        validateOrderStatusTransition(order.getStatus(), newStatus);

        order.setStatus(newStatus);

        if (newStatus == OrderStatus.DELIVERED && order.getPaymentMethod() == PaymentMethod.CASH_ON_DELIVERY) {
            order.setPaymentStatus(PaymentStatus.PAID);
        }

        if (newStatus == OrderStatus.CANCELED) {
            restoreStock(order);
        }

        Order savedOrder = orderRepository.save(order);

        return mapToOrderResponseDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponseDto cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (!order.getCustomer().getId().equals(userId)) {
            throw new AccessDeniedException("You can only cancel your own order");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Cannot cancel order in " + order.getStatus() + " status. " +
                    "You can only cancel when order is pending");
        }

        order.setStatus(OrderStatus.CANCELED);

        restoreStock(order);

        Order savedOrder = orderRepository.save(order);

        return mapToOrderResponseDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderCountResponseDto getShopOrderCount(Long sellerId) {
        Shop shop = shopRepository.findByOwnerId(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("You don't have a shop yet."));

        Long shopId = shop.getId();

        return OrderCountResponseDto.builder()
                .totalOrders(orderRepository.countByShopId(shopId))
                .pendingOrders(orderRepository.countByShopIdAndStatus(shopId, OrderStatus.PENDING))
                .confirmedOrders(orderRepository.countByShopIdAndStatus(shopId, OrderStatus.CONFIRMED))
                .processingOrders(orderRepository.countByShopIdAndStatus(shopId, OrderStatus.PROCESSING))
                .outForDeliveryOrders(orderRepository.countByShopIdAndStatus(shopId, OrderStatus.OUT_FOR_DELIVERY))
                .deliveredOrders(orderRepository.countByShopIdAndStatus(shopId, OrderStatus.DELIVERED))
                .cancelledOrders(orderRepository.countByShopIdAndStatus(shopId, OrderStatus.CANCELED))
                .build();
    }

    private OrderStatus parseOrderStatus(String status) {
        try {
            return OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid order status: " + status);
        }
    }

    private void validateOrderStatusTransition(OrderStatus current, OrderStatus next) {
        if (current == OrderStatus.DELIVERED) {
            throw new BadRequestException("Order is already delivered");
        }

        if (current == OrderStatus.CANCELED) {
            throw new BadRequestException("Order is already cancelled");
        }

        if (current == next) {
            throw new BadRequestException("Order is already " + current.name());
        }

        if (next == OrderStatus.CANCELED) {
            if (current != OrderStatus.PENDING && current != OrderStatus.CONFIRMED) {
                throw new BadRequestException("Cannot cancel order in " + current.name() + " status. " +
                        "Cancellation is only allowed when PENDING or CONFIRMED.");
            }
            return;
        }

        if (next.ordinal() != current.ordinal() + 1) {
            throw new BadRequestException("Cannot change status from " + current.name() + " to " + next.name() + ". " +
                    "Next valid status: " + getNextValidStatus(current));
        }
    }

    private String getNextValidStatus(OrderStatus current) {
        return switch (current) {
            case PENDING -> "CONFIRMED or CANCELED";
            case CONFIRMED -> "PROCESSING or CANCELED";
            case PROCESSING -> "OUT_FOR_DELIVERY";
            case OUT_FOR_DELIVERY -> "DELIVERED";
            default -> "non (final status)";
        };
    }

    private void restoreStock(Order order) {
        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProduct();
            product.setQuantity(product.getQuantity() + orderItem.getQuantity());

            productRepository.save(product);
        }
    }


    private OrderResponseDto mapToOrderResponseDto(Order order) {
        List<OrderItemResponseDto> orderItems = order.getOrderItems().stream()
                .map(this::mapToOrderItemResponseDto)
                .toList();

        return OrderResponseDto.builder()
                .id(order.getId())
                .status(order.getStatus().name())
                .paymentStatus(order.getPaymentStatus().name())
                .paymentMethod(order.getPaymentMethod().name())
                .totalAmount(order.getTotalAmount())
                .shopId(order.getShop().getId())
                .shopName(order.getShop().getName())
                .addressId(order.getAddress().getId())
                .addressLine(order.getAddress().getAddressLine())
                .city(order.getAddress().getCity())
                .orderItems(orderItems)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private OrderItemResponseDto mapToOrderItemResponseDto(OrderItem orderItem) {
        BigDecimal subtotal = orderItem.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()));

        return OrderItemResponseDto.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProduct().getId())
                .productName(orderItem.getProduct().getName())
                .unit(orderItem.getProduct().getUnit())
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .subtotal(subtotal)
                .build();
    }

}
