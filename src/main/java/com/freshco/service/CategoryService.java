package com.freshco.service;

import com.freshco.dto.request.CategoryRequestDto;
import com.freshco.dto.response.CategoryResponseDto;

import java.util.List;

public interface CategoryService {

    CategoryResponseDto createCategory(CategoryRequestDto request);

    List<CategoryResponseDto> getAllCategories();

    CategoryResponseDto getCategoryById(Long id);

    CategoryResponseDto updateCategory(Long id, CategoryRequestDto request);

    void deleteCategory(Long id);

}
