package com.freshco.controller;

import com.freshco.dto.request.AddToCartRequestDto;
import com.freshco.dto.request.UpdateCartItemQuantityRequestDto;
import com.freshco.dto.response.CartResponseDto;
import com.freshco.security.CustomUserDetails;
import com.freshco.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "8. Cart", description = "Shopping cart operations")
public class CartController {

    private final CartService cartService;

    @PostMapping("/items")
    @Operation(summary = "Add item to cart", description = "Adds a product to cart. Enforces single-shop rule.")
    public ResponseEntity<CartResponseDto> addToCart(
            @Valid @RequestBody AddToCartRequestDto request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        CartResponseDto response = cartService.addToCart(request, customUserDetails.getUser().getId());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "View cart")
    public ResponseEntity<CartResponseDto> getCart(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        CartResponseDto response = cartService.getCart(customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/items/{id}")
    @Operation(summary = "Update item quantity")
    public ResponseEntity<CartResponseDto> updateCartItemQuantity(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCartItemQuantityRequestDto request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        CartResponseDto response = cartService.updateCartItemQuantity(id, request.getQuantity(), customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/items/{id}")
    @Operation(summary = "Remove item from cart")
    public ResponseEntity<CartResponseDto> removeCartItem(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        CartResponseDto response = cartService.removeCartItem(id, customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    @Operation(summary = "Clear cart", description = "Removes all items and resets shop association")
    public ResponseEntity<CartResponseDto> clearCart(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        CartResponseDto response = cartService.clearCart(customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

}
