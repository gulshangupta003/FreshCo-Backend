package com.freshco.service.impl;

import com.freshco.dto.request.PlaceOrderRequestDto;
import com.freshco.dto.response.OrderItemResponseDto;
import com.freshco.dto.response.OrderResponseDto;
import com.freshco.entity.*;
import com.freshco.exception.BadRequestException;
import com.freshco.exception.ResourceNotFoundException;
import com.freshco.repository.AddressRepository;
import com.freshco.repository.CartRepository;
import com.freshco.repository.OrderRepository;
import com.freshco.repository.ProductRepository;
import com.freshco.service.OrderService;
import lombok.RequiredArgsConstructor;
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
    public List<OrderResponseDto> getMyOrders(Long userId) {
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToOrderResponseDto)
                .toList();
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
