package com.freshco.controller;

import com.freshco.dto.request.ProductRequestDto;
import com.freshco.dto.response.PagedResponseDto;
import com.freshco.dto.response.ProductResponseDto;
import com.freshco.security.CustomUserDetails;
import com.freshco.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductResponseDto> createProduct(
            @Valid @RequestBody ProductRequestDto request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        ProductResponseDto response = productService.createProduct(request, customUserDetails.getUser().getId());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        ProductResponseDto response = productService.getProductById(id);

        return ResponseEntity.ok(response);
    }

//    @GetMapping
//    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
//        List<ProductResponseDto> response = productService.getAllProducts();
//
//        return ResponseEntity.ok(response);
//    }

    @GetMapping
    public ResponseEntity<PagedResponseDto<ProductResponseDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PagedResponseDto<ProductResponseDto> response = productService.getAllProducts(page, size);

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDto request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        ProductResponseDto response = productService.updateProduct(id, request, customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        productService.deleteProduct(id, customUserDetails.getUser().getId());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDto>> searchProducts(@RequestParam(name = "q") String keyword) {
        List<ProductResponseDto> response = productService.searchProducts(keyword);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/active")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ProductResponseDto> toggleProductActive(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        ProductResponseDto response = productService.toggleProductActive(id, customUserDetails.getUser().getId());

        return ResponseEntity.ok(response);
    }

}
