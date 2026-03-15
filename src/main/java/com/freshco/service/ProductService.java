package com.freshco.service;

import com.freshco.dto.request.ProductRequestDto;
import com.freshco.dto.response.PagedResponseDto;
import com.freshco.dto.response.ProductResponseDto;

import java.util.List;

public interface ProductService {

    ProductResponseDto createProduct(ProductRequestDto request, Long sellerId);

    ProductResponseDto getProductById(Long productId);

//    List<ProductResponseDto> getAllProducts();

    PagedResponseDto<ProductResponseDto> getAllProducts(int page, int size);

    List<ProductResponseDto> getProductsByShopId(Long shopId);

    ProductResponseDto updateProduct(Long productId, ProductRequestDto request, Long sellerId);

    void deleteProduct(Long productId, Long sellerId);

    List<ProductResponseDto> getProductsByCategoryId(Long categoryId);

    List<ProductResponseDto> searchProducts(String keyword);

    ProductResponseDto toggleProductActive(Long productId, Long sellerId);

}
