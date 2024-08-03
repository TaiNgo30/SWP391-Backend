package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.requests.CategoryRequest;
import com.wha.warehousemanagement.dtos.responses.CategoryResponse;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.mappers.CategoryMapper;
import com.wha.warehousemanagement.models.Category;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public ResponseObject<?> addCategory(CategoryRequest request) {
        try {
            if (request.getName() == null || request.getName().trim().isEmpty()) {
                throw new CustomException(ErrorCode.CATEGORY_NAME_BLANK);
            } else if (categoryRepository.findByName(request.getName()).isPresent()) {
                throw new CustomException(ErrorCode.CATEGORY_ALREADY_EXISTS);
            }
            Category category = new Category();
            category.setName(request.getName());
            category.setDescription(request.getDescription());
            categoryRepository.save(category);
            Optional<CategoryResponse> response = getLastCategoryResponse();
            if (response.isEmpty()) {
                throw new CustomException(ErrorCode.CATEGORY_ADD_FAILED);
            }
            return new ResponseObject<>(HttpStatus.OK.value(),
                    "Category added successfully",
                    response.get());
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to add category", null);
        }
    }

    private Optional<CategoryResponse> getLastCategoryResponse() {
        List<CategoryResponse> responses = categoryRepository.getAllCategoryResponses();
        if (!responses.isEmpty()) {
            return Optional.of(responses.get(0));
        } else {
            return Optional.empty();
        }
    }

    public ResponseObject<?> getAllCategories() {
        try {
            List<CategoryResponse> list = categoryRepository.findAll()
                    .stream()
                    .map(categoryMapper::toDto)
                    .collect(Collectors.toList());
            if (list.isEmpty()) {
                throw new CustomException(ErrorCode.CATEGORY_NOT_FOUND);
            }
            return new ResponseObject<>(HttpStatus.OK.value(), "Get all categories successfully", list);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get all categories", null);
        }
    }

    public ResponseObject<?> getCategoryById(int id) {
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
            CategoryResponse response = categoryMapper.toDto(category);
            return new ResponseObject<>(HttpStatus.OK.value(), "Get category by id successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get category by id", null);
        }
    }

    public ResponseObject<?> updateCategory(int id, CategoryRequest request) {
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
            if (request.getName() != null && !request.getName().trim().isEmpty()) {
//                if (categoryRepository.existsByName(request.getName())) {
//                    throw new CustomException(ErrorCode.CATEGORY_ALREADY_EXISTS);
//                }
                category.setName(request.getName());
            }
            if (request.getDescription() != null && !request.getDescription().trim().isEmpty()) {
                category.setDescription(request.getDescription());
            }
            categoryRepository.save(category);
            CategoryResponse response = categoryMapper.toDto(category);
            return new ResponseObject<>(HttpStatus.OK.value(), "Updated category successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to update category", null);
        }
    }

    public ResponseObject<?> deleteCategoryById(int id) {
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
            categoryRepository.delete(category);
            return new ResponseObject<>(HttpStatus.OK.value(), "Deleted category successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete category", null);
        }
    }

    public ResponseObject<?> deleteAllCategories() {
        try {
            List<Category> list = categoryRepository.findAll();
            if (!list.isEmpty()) {
                categoryRepository.deleteAll();
                return new ResponseObject<>(HttpStatus.OK.value(), "Deleted all categories successfully", null);
            } else {
                return new ResponseObject<>(HttpStatus.NO_CONTENT.value(), "No categories to delete", null);
            }
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete categories", null);
        }
    }

    public ResponseObject<List<CategoryResponse>> getAllSortedCategoryWithSearch(String search) {
        try {
            List<CategoryResponse> res = categoryMapper.toDto(categoryRepository.findAllCategoriesWithSearch(search));
            return new ResponseObject<>(HttpStatus.OK.value(), "Get categories successfully", res);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get all sorted categories with search", null);
        }
    }

}
