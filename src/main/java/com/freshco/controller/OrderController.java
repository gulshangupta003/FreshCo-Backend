package com.freshco.controller;

import com.freshco.dto.request.PlaceOrderRequestDto;
import com.freshco.dto.request.UpdateOrderStatusRequestDto;
import com.freshco.dto.response.OrderResponseDto;
import com.freshco.dto.response.PagedResponseDto;
import com.freshco.security.CustomUserDetails;
import com.freshco.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "9. Order", description = "Order payment and management")
public class OrderController {

    private final OrderService orderService;

    // ToDo: While checkout product should be active
    @PostMapping
    @Operation(summary = "Place order", description = "Creates order from cart items with price locking")
    public ResponseEntity<OrderResponseDto> placeOrder(
            @Valid @RequestBody PlaceOrderRequestDto request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        OrderResponseDto response = orderService.placeOrder(request, customUserDetails.getUser().getId());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order details", description = "Accessible by the customer who placed it or the shop owner")
    public ResponseEntity<OrderResponseDto> getOrderById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        OrderResponseDto response = orderService.getOrderById(id, customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get my orders", description = "Returns paginated orders for the logged-in customer")
    public ResponseEntity<PagedResponseDto<OrderResponseDto>> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        PagedResponseDto<OrderResponseDto> response = orderService.getMyOrders(
                customUserDetails.getUser().getId(), page, size);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Update order status", description = "Seller advances order through the status workflow")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequestDto request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        OrderResponseDto response = orderService.updateOrderStatus(
                id, request.getStatus(), customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel order", description = "Customer can cancel from PENDING status only")
    public ResponseEntity<OrderResponseDto> cancelOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        OrderResponseDto response = orderService.cancelOrder(id, customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

}
