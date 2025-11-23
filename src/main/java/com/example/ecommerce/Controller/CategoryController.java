package com.example.ecommerce.Controller;

import com.example.ecommerce.Model.Category;
import com.example.ecommerce.Services.CategoryService;
import com.example.ecommerce.config.AppConst;
import com.example.ecommerce.payload.CategoryDTO;
import com.example.ecommerce.payload.CategoryResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.ecommerce.config.AppConst.PAGE_NUMBER;
import static com.example.ecommerce.config.AppConst.PAGE_SIZE;

@RestController
@EnableMethodSecurity
public class CategoryController {

    //Constructor Injection of that bean
//    private CategoryService categoryService;
//    public CategoryController(CategoryService categoryService) {
//        this.categoryService = categoryService;
//    }

    //Or You can do FIELD Injection
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/api/public/categories")
    public CategoryResponse getCategory(@RequestParam(name="pageNumber", defaultValue = AppConst.PAGE_NUMBER, required = false) Integer pageNumber,
                                        @RequestParam (name="pageSize", defaultValue = AppConst.PAGE_SIZE, required = false) Integer pageSize,
                                        @RequestParam (name="sortBy", defaultValue = "categoryId", required = false) String sortBy,
                                        @RequestParam (name="sortOrder", defaultValue = AppConst.SORT_ORDER, required = false) String sortOrder) {

        CategoryResponse res = categoryService.getAllCategories(pageNumber,pageSize, sortBy, sortOrder);
        return res;
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("api/admin/categories")
    public ResponseEntity<String> addcategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>("Added Successfully",HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/api/admin/categories/{id}")
    public ResponseEntity<String> deleteCategory(@Valid @PathVariable Long id) {
            String status = categoryService.removeCategory(id);
            return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/api/admin/categories/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO, @PathVariable Long id) {
            CategoryDTO saved = categoryService.updateCategory(categoryDTO, id);
            return new ResponseEntity<>(saved, HttpStatus.OK);
    }
}
