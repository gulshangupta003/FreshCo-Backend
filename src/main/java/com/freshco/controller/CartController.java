package com.freshco.controller;

import com.freshco.dto.request.AddToCartRequestDto;
import com.freshco.dto.request.UpdateCartItemQuantityRequestDto;
import com.freshco.dto.response.CartResponseDto;
import com.freshco.security.CustomUserDetails;
import com.freshco.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/items")
    public ResponseEntity<CartResponseDto> addToCart(
            @Valid @RequestBody AddToCartRequestDto request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        CartResponseDto response = cartService.addToCart(request, customUserDetails.getUser().getId());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<CartResponseDto> getCart(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        CartResponseDto response = cartService.getCart(customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/items/{id}")
    public ResponseEntity<CartResponseDto> updateCartItemQuantity(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCartItemQuantityRequestDto request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        CartResponseDto response = cartService.updateCartItemQuantity(id, request.getQuantity(), customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<CartResponseDto> removeCartItem(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        CartResponseDto response = cartService.removeCartItem(id, customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

}
