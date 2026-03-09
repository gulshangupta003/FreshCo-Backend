package com.freshco.controller;

import com.freshco.dto.request.PlaceOrderRequestDto;
import com.freshco.dto.response.OrderResponseDto;
import com.freshco.security.CustomUserDetails;
import com.freshco.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDto> placeOrder(
            @Valid @RequestBody PlaceOrderRequestDto request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        OrderResponseDto response = orderService.placeOrder(request, customUserDetails.getUser().getId());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        OrderResponseDto response = orderService.getOrderById(id, customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

}
