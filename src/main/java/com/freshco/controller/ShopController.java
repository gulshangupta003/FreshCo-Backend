package com.freshco.controller;

import com.freshco.dto.ShopRequestDto;
import com.freshco.dto.ShopResponseDto;
import com.freshco.security.CustomUserDetails;
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

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ShopResponseDto> createShop(
            @Valid @RequestBody ShopRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ShopResponseDto response = shopService.createShop(request, userDetails.getUser().getId());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<ShopResponseDto> getShopById(@PathVariable Long id) {
        ShopResponseDto response = shopService.getShopById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ShopResponseDto>> getAllShops() {
        List<ShopResponseDto> response = shopService.getALlShops();

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

}
