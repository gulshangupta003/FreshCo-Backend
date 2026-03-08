package com.freshco.service.impl;

import com.freshco.dto.request.AddToCartRequestDto;
import com.freshco.dto.response.CartItemResponseDto;
import com.freshco.dto.response.CartResponseDto;
import com.freshco.entity.Cart;
import com.freshco.entity.CartItem;
import com.freshco.entity.Product;
import com.freshco.entity.User;
import com.freshco.exception.BadRequestException;
import com.freshco.exception.ResourceNotFoundException;
import com.freshco.repository.CartItemRepository;
import com.freshco.repository.CartRepository;
import com.freshco.repository.ProductRepository;
import com.freshco.repository.UserRepository;
import com.freshco.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final ProductRepository productRepository;

    private final CartRepository cartRepository;

    private final UserRepository userRepository;

    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional
    public CartResponseDto addToCart(AddToCartRequestDto request, Long userId) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        if (!product.isActive()) {
            throw new BadRequestException("Product is not available");
        }

        if (product.getQuantity() < request.getQuantity()) {
            throw new BadRequestException("Not enough stock. Available: " + product.getQuantity());
        }

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

                    return cartRepository.save(
                            Cart.builder()
                                    .user(user)
                                    .shop(product.getShop())
                                    .build()
                    );
                });

        if (cart.getShop() != null && !cart.getShop().getId().equals(product.getShop().getId())) {
            throw new BadRequestException(
                    "Your cart has item from shop: " + cart.getShop().getName()
                            + ". Clear cart to add item from another shop."
            );
        }

        if (cart.getShop() == null) {
            cart.setShop(product.getShop());
        }

        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());
        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            int newQuantity = cartItem.getQuantity() + request.getQuantity();

            if (product.getQuantity() < newQuantity) {
                throw new BadRequestException("Not enough stock. Available: " + product.getQuantity());
            }

            cartItem.setQuantity(newQuantity);

            cartItemRepository.save(cartItem);
        } else {
            CartItem newCartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();

            cart.getItems().add(newCartItem);
        }

        Cart savedCart = cartRepository.save(cart);

        return mapToCartResponseDto(savedCart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponseDto getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElse(null);

        if (cart == null) {
            return CartResponseDto.builder()
                    .items(List.of())
                    .totalItems(0)
                    .totalAmount(BigDecimal.ZERO)
                    .build();
        }

        return mapToCartResponseDto(cart);
    }

    private CartResponseDto mapToCartResponseDto(Cart cart) {
        List<CartItemResponseDto> cartItems = cart.getItems().stream()
                .map(this::mapToCartItemResponseDto)
                .toList();

        BigDecimal totalAmount = cartItems.stream()
                .map(CartItemResponseDto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponseDto.builder()
                .id(cart.getId())
                .shopId(cart.getShop() != null ? cart.getShop().getId() : null)
                .shopName(cart.getShop() != null ? cart.getShop().getName() : null)
                .items(cartItems)
                .totalItems(cartItems.size())
                .totalAmount(totalAmount)
                .build();
    }

    private CartItemResponseDto mapToCartItemResponseDto(CartItem cartItem) {
        Product product = cartItem.getProduct();
        BigDecimal subtotal = product.getPrice()
                .multiply(BigDecimal.valueOf(cartItem.getQuantity()));

        return CartItemResponseDto.builder()
                .id(cartItem.getId())
                .productId(cartItem.getProduct().getId())
                .productName(cartItem.getProduct().getName())
                .productPrice(cartItem.getProduct().getPrice())
                .unit(product.getUnit())
                .imageUrl(product.getImageUrl())
                .quantity(cartItem.getQuantity())
                .subtotal(subtotal)
                .build();
    }

}
