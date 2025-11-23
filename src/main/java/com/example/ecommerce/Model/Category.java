package com.example.ecommerce.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  //Unique values auto-gen by db
    private Long categoryId;

    @NotBlank
    @Size(min = 4, message = "Has to be more than 3 chars!!!")
    private String categoryName;

    @OneToMany(mappedBy = "category")
    private List<Product> Products;
}
