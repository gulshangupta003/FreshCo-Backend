package com.freshco.service.impl;

import com.freshco.dto.request.CategoryRequestDto;
import com.freshco.dto.response.CategoryResponseDto;
import com.freshco.entity.Category;
import com.freshco.exception.DuplicateResourceException;
import com.freshco.exception.ResourceNotFoundException;
import com.freshco.repository.CategoryRepository;
import com.freshco.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryResponseDto createCategory(CategoryRequestDto request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new DuplicateResourceException("Category " + request.getName() + " already exists");
        }

        Category category = Category.builder()
                .name(request.getName())
                .imageUrl(request.getImageUrl())
                .build();

        Category savedCategory = categoryRepository.save(category);

        return mapToCategoryResponseDto(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        return categories.stream()
                .map(this::mapToCategoryResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        return mapToCategoryResponseDto(category);
    }

    @Override
    @Transactional
    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        String newName = request.getName().toLowerCase().trim();
        if (!category.getName().equalsIgnoreCase(newName) && categoryRepository.existsByNameIgnoreCase(newName)) {
            throw new DuplicateResourceException("Category already exists with name: " + newName);
        }

        category.setName(newName);
        category.setImageUrl(request.getImageUrl());

        Category savedCategory = categoryRepository.save(category);

        return mapToCategoryResponseDto(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.findById(id)
                .ifPresent(categoryRepository::delete);
    }

    private CategoryResponseDto mapToCategoryResponseDto(Category category) {
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .imageUrl(category.getImageUrl())
                .createdAt(category.getCreatedAt())
                .build();
    }

}
