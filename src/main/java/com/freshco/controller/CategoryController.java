package com.freshco.controller;

import com.freshco.dto.request.CategoryRequestDto;
import com.freshco.dto.response.CategoryResponseDto;
import com.freshco.dto.response.PagedResponseDto;
import com.freshco.dto.response.ProductResponseDto;
import com.freshco.service.CategoryService;
import com.freshco.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "5. Category", description = "Product categories")
public class CategoryController {

    private final CategoryService categoryService;

    private final ProductService productService;

    @PostMapping
    @Operation(summary = "Create a category")
    public ResponseEntity<CategoryResponseDto> createCategory(@Valid @RequestBody CategoryRequestDto request) {
        CategoryResponseDto response = categoryService.createCategory(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all categories")
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
        List<CategoryResponseDto> response = categoryService.getAllCategories();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID")
    public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable Long id) {
        CategoryResponseDto response = categoryService.getCategoryById(id);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category")
    public ResponseEntity<CategoryResponseDto> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequestDto request
    ) {
        CategoryResponseDto response = categoryService.updateCategory(id, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/products")
    @Operation(summary = "Get category products")
    public ResponseEntity<PagedResponseDto<ProductResponseDto>> getProductsByCategoryId(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PagedResponseDto<ProductResponseDto> response = productService.getProductsByCategoryId(id, page, size);

        return ResponseEntity.ok(response);
    }

}
