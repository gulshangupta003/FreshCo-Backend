package com.freshco.controller;

import com.freshco.dto.request.ShopRequestDto;
import com.freshco.dto.response.*;
import com.freshco.security.CustomUserDetails;
import com.freshco.service.OrderService;
import com.freshco.service.ProductService;
import com.freshco.service.ShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "4. Shop", description = "Shop management and browsing")
public class ShopController {

    private final ShopService shopService;
    private final ProductService productService;
    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Create a shop", description = "Creates a new shop for the logged-in seller (one shop per seller)")
    public ResponseEntity<ShopResponseDto> createShop(
            @Valid @RequestBody ShopRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ShopResponseDto response = shopService.createShop(request, userDetails.getUser().getId());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all shops")
    public ResponseEntity<List<ShopResponseDto>> getAllShops() {
        List<ShopResponseDto> response = shopService.getAllShops();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Update shop", description = "Updates the shop owned by the logged-in seller")
    public ResponseEntity<ShopResponseDto> updateShop(
            @PathVariable Long id,
            @Valid @RequestBody ShopRequestDto request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        ShopResponseDto response = shopService.updateShop(id, request, customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Delete shop", description = "Deletes the shop owned by the logged-in seller")
    public ResponseEntity<Void> deleteShop(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        shopService.deleteShop(id, customUserDetails.getUser().getId());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{shopId}/products")
    @Operation(summary = "Get shop products", description = "Returns paginated products for a specific shop")
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
    @Operation(summary = "Get shop orders", description = "Returns paginated orders for the logged-in seller's shop")
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
    @Operation(summary = "Get my shop", description = "Returns the logged-in seller's shop")
    public ResponseEntity<ShopResponseDto> getMyShop(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        ShopResponseDto response = shopService.getMyShop(customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/orders/count")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Get my shop order counts", description = "Returns order count grouped by status for seller dashboard")
    public ResponseEntity<OrderCountResponseDto> getShopOrderCount(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        OrderCountResponseDto response = orderService.getShopOrderCount(customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/pincode/{pincode}")
    @Operation(summary = "Get shop by pincode", description = "Returns paginated shops in a specific pincode area")
    public ResponseEntity<PagedResponseDto<ShopResponseDto>> getShopByPincode(
            @PathVariable String pincode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(shopService.getShopByPincode(pincode, page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get shop by ID")
    public ResponseEntity<ShopResponseDto> getShopById(@PathVariable Long id) {
        ShopResponseDto response = shopService.getShopById(id);

        return ResponseEntity.ok(response);
    }

}
