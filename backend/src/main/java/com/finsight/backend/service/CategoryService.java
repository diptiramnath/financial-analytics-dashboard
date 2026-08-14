package com.finsight.backend.service;

import com.finsight.backend.dto.CategoryResponse;
import com.finsight.backend.dto.CreateCategoryRequest;
import com.finsight.backend.entity.Category;
import com.finsight.backend.entity.User;
import com.finsight.backend.exception.CategoryNotFoundException;
import com.finsight.backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CurrentUserService currentUserService;

    public CategoryService(CategoryRepository categoryRepository,
                           CurrentUserService currentUserService) {
        this.categoryRepository = categoryRepository;
        this.currentUserService = currentUserService;
    }

    public CategoryResponse createCategory(CreateCategoryRequest request) {

        User currentUser = currentUserService.getCurrentUser();

        Category category = Category.builder()
                .userId(currentUser.getId())
                .code(request.getCode())
                .name(request.getName())
                .type(request.getType())
                .icon(request.getIcon())
                .color(request.getColor())
                .systemCategory(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Category saved = categoryRepository.save(category);

        return mapToResponse(saved);
    }

    public List<CategoryResponse> getCategories() {

        User currentUser = currentUserService.getCurrentUser();

        List<Category> categories = categoryRepository.findAll()
                .stream()
                .filter(category ->
                        category.isSystemCategory()
                                || currentUser.getId().equals(category.getUserId()))
                .toList();

        return categories.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public CategoryResponse getCategory(String id) {

        User currentUser = currentUserService.getCurrentUser();

        Category category = categoryRepository.findById(id)
                .filter(c ->
                        c.isSystemCategory()
                                || currentUser.getId().equals(c.getUserId()))
                .orElseThrow(() ->
                        new CategoryNotFoundException("Category not found"));

        return mapToResponse(category);
    }

    public void deleteCategory(String id) {

        User currentUser = currentUserService.getCurrentUser();

        Category category = categoryRepository.findById(id)
                .filter(c ->
                        !c.isSystemCategory()
                                && currentUser.getId().equals(c.getUserId()))
                .orElseThrow(() ->
                        new CategoryNotFoundException("Category not found"));

        categoryRepository.delete(category);
    }

    private CategoryResponse mapToResponse(Category category) {

        return CategoryResponse.builder()
                .id(category.getId())
                .code(category.getCode())
                .name(category.getName())
                .type(category.getType())
                .icon(category.getIcon())
                .color(category.getColor())
                .systemCategory(category.isSystemCategory())
                .build();
    }
}