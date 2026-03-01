package com.freshco.service.impl;

import com.freshco.dto.request.CategoryRequestDto;
import com.freshco.dto.request.CategoryResponseDto;
import com.freshco.entity.Category;
import com.freshco.exception.DuplicateResourceException;
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

    private CategoryResponseDto mapToCategoryResponseDto(Category category) {
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .imageUrl(category.getImageUrl())
                .createdAt(category.getCreatedAt())
                .build();
    }

}
