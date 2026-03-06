package com.freshco.controller;

import com.freshco.dto.request.AddressRequestDto;
import com.freshco.dto.response.AddressResponseDto;
import com.freshco.security.CustomUserDetails;
import com.freshco.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<AddressResponseDto> createAddress(
            @Valid @RequestBody AddressRequestDto request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        AddressResponseDto response = addressService.createAddress(request, customUserDetails.getUser().getId());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AddressResponseDto>> getMyAddresses(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        List<AddressResponseDto> response = addressService.getMyAddresses(customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponseDto> updateResponse(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequestDto request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        AddressResponseDto response = addressService.updateAddress(id, request, customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

}
