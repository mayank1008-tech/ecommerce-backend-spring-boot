package com.example.ecommerce.Repository;

import com.example.ecommerce.Model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Category findByCategoryName(String categoryName);  //Hamne sirf id se indexing karne ki permission di hai so
                                                       //naam se karne ke liye method define kra
}
