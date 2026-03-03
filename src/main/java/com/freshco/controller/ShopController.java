package com.freshco.controller;

import com.freshco.dto.ShopRequestDto;
import com.freshco.dto.ShopResponseDto;
import com.freshco.security.CustomUserDetails;
import com.freshco.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ShopResponseDto> createShop(
            @RequestBody ShopRequestDto request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        ShopResponseDto response = shopService.createShop(request, userDetails.getUsername());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
