package com.freshco.service.impl;

import com.freshco.dto.request.ProductRequestDto;
import com.freshco.dto.response.ProductResponseDto;
import com.freshco.entity.Category;
import com.freshco.entity.Product;
import com.freshco.entity.Shop;
import com.freshco.exception.ResourceNotFoundException;
import com.freshco.repository.CategoryRepository;
import com.freshco.repository.ProductRepository;
import com.freshco.repository.ShopRepository;
import com.freshco.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final ShopRepository shopRepository;

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto request, Long sellerId) {
        Shop shop = shopRepository.findByOwnerId(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("You don't have a shop yet. Create shop first."));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .unit(request.getUnit())
                .imageUrl(request.getImageUrl())
                .shop(shop)
                .category(category)
                .build();

        Product savedProduct = productRepository.save(product);

        return mapToProductResponseDto(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getProductById(Long productId) {
        Product product = findProductById(productId);

        return mapToProductResponseDto(product);
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
    }

    private ProductResponseDto mapToProductResponseDto(Product product) {
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .unit(product.getUnit())
                .imageUrl(product.getImageUrl())
                .active(product.isActive())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .shopId(product.getShop().getId())
                .shopName(product.getShop().getName())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .build();
    }
}
