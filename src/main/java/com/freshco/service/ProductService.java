package com.freshco.service;

import com.freshco.dto.request.ProductRequestDto;
import com.freshco.dto.response.ProductResponseDto;

import java.util.List;

public interface ProductService {

    ProductResponseDto createProduct(ProductRequestDto request, Long sellerId);

    ProductResponseDto getProductById(Long productId);

    List<ProductResponseDto> getAllProducts();

}
