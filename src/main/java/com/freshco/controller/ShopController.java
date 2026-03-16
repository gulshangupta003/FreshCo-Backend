package com.freshco.controller;

import com.freshco.dto.request.ShopRequestDto;
import com.freshco.dto.response.*;
import com.freshco.security.CustomUserDetails;
import com.freshco.service.OrderService;
import com.freshco.service.ProductService;
import com.freshco.service.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    private final ProductService productService;

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ShopResponseDto> createShop(
            @Valid @RequestBody ShopRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ShopResponseDto response = shopService.createShop(request, userDetails.getUser().getId());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ShopResponseDto>> getAllShops() {
        List<ShopResponseDto> response = shopService.getAllShops();

        return ResponseEntity.ok(response);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ShopResponseDto> updateShop(
            @PathVariable Long id,
            @Valid @RequestBody ShopRequestDto request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        ShopResponseDto response = shopService.updateShop(id, request, customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Void> deleteShop(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        shopService.deleteShop(id, customUserDetails.getUser().getId());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{shopId}/products")
    public ResponseEntity<PagedResponseDto<ProductResponseDto>> getProductsByShopId(
            @PathVariable Long shopId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PagedResponseDto<ProductResponseDto> response = productService.getProductsByShopId(shopId, page, size);

        return ResponseEntity.ok(response);
    }

    @GetMapping("{shopId}/orders")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<PagedResponseDto<OrderResponseDto>> getShopOrders(
            @PathVariable Long shopId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PagedResponseDto<OrderResponseDto> response = orderService.getShopOrders(
                shopId, customUserDetails.getUser().getId(), page, size
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ShopResponseDto> getMyShop(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ShopResponseDto response = shopService.getMyShop(customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/orders/count")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<OrderCountResponseDto> getShopOrderCount(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        OrderCountResponseDto response = orderService.getShopOrderCount(customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("{id}")
    public ResponseEntity<ShopResponseDto> getShopById(@PathVariable Long id) {
        ShopResponseDto response = shopService.getShopById(id);

        return ResponseEntity.ok(response);
    }

}
