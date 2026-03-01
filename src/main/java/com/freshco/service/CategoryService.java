package com.freshco.service;

import com.freshco.dto.request.CategoryRequestDto;
import com.freshco.dto.request.CategoryResponseDto;

import java.util.List;

public interface CategoryService {

    CategoryResponseDto createCategory(CategoryRequestDto request);

    List<CategoryResponseDto> getAllCategories();

}
