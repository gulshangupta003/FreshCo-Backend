package com.freshco.controller;

import com.freshco.dto.request.AddressRequestDto;
import com.freshco.dto.response.AddressResponseDto;
import com.freshco.security.CustomUserDetails;
import com.freshco.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "7. Address", description = "Delivery address management")
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    @Operation(summary = "Add address", description = "Adds a delivery address (max 5 per user)")
    public ResponseEntity<AddressResponseDto> createAddress(
            @Valid @RequestBody AddressRequestDto request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        AddressResponseDto response = addressService.createAddress(request, customUserDetails.getUser().getId());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get my addresses")
    public ResponseEntity<List<AddressResponseDto>> getMyAddresses(
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        List<AddressResponseDto> response = addressService.getMyAddresses(customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update address")
    public ResponseEntity<AddressResponseDto> updateResponse(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequestDto request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        AddressResponseDto response = addressService.updateAddress(id, request, customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete address", description = "Deletes address and auto-promotes default if needed")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        addressService.deleteAddress(id, customUserDetails.getUser().getId());

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/default")
    @Operation(summary = "Set default address")
    public ResponseEntity<AddressResponseDto> setDefaultAddress(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        AddressResponseDto response = addressService.setDefaultAddress(id, customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

}
