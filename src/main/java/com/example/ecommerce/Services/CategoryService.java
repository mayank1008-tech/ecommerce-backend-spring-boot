package com.example.ecommerce.Services;

import com.example.ecommerce.Model.Category;
import com.example.ecommerce.payload.CategoryDTO;
import com.example.ecommerce.payload.CategoryResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface CategoryService {
    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    void createCategory(CategoryDTO categoryDTO);
    String removeCategory(Long id);
    CategoryDTO updateCategory(CategoryDTO categoryDTO,Long id);
}
