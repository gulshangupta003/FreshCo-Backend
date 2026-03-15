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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToProductResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProductsByShopId(Long shopId) {
        return productRepository.findByShopId(shopId).stream()
                .map(this::mapToProductResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public ProductResponseDto updateProduct(Long productId, ProductRequestDto request, Long sellerId) {
        Product product = findProductById(productId);

        if (!product.getShop().getOwner().getId().equals(sellerId)) {
            throw new AccessDeniedException("You can only update products in our own shop");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setUnit(request.getUnit());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);

        Product updatedProduct = productRepository.save(product);

        return mapToProductResponseDto(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId, Long sellerId) {
        Product product = findProductById(productId);

        if (!product.getShop().getOwner().getId().equals(sellerId)) {
            throw new AccessDeniedException("You can only delete products in your own shop");
        }

        productRepository.delete(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(this::mapToProductResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }

        return productRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(this::mapToProductResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public ProductResponseDto toggleProductActive(Long productId, Long sellerId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (!product.getShop().getOwner().getId().equals(sellerId)) {
            throw new AccessDeniedException("You can only update product in your own shop");
        }

        product.setActive(!product.isActive());

        Product savedProduct = productRepository.save(product);

        return mapToProductResponseDto(savedProduct);
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
