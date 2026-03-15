package com.freshco.service;

import com.freshco.dto.request.ProductRequestDto;
import com.freshco.dto.response.PagedResponseDto;
import com.freshco.dto.response.ProductResponseDto;

import java.util.List;

public interface ProductService {

    ProductResponseDto createProduct(ProductRequestDto request, Long sellerId);

    ProductResponseDto getProductById(Long productId);

    PagedResponseDto<ProductResponseDto> getAllProducts(int page, int size);

    PagedResponseDto<ProductResponseDto> getProductsByShopId(Long shopId, int page, int size);

    ProductResponseDto updateProduct(Long productId, ProductRequestDto request, Long sellerId);

    void deleteProduct(Long productId, Long sellerId);

    List<ProductResponseDto> getProductsByCategoryId(Long categoryId);

    PagedResponseDto<ProductResponseDto> searchProducts(String keyword, int page, int size);

    ProductResponseDto toggleProductActive(Long productId, Long sellerId);

}
