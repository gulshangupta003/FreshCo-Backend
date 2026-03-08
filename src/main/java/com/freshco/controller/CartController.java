package com.freshco.controller;

import com.freshco.dto.request.AddToCartRequestDto;
import com.freshco.dto.response.CartResponseDto;
import com.freshco.security.CustomUserDetails;
import com.freshco.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
