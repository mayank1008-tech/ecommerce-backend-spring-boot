package com.example.ecommerce.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long ProductId;
    @NotBlank
    @Size(min = 4, message = "Has to be more than 3 chars!!!")
    private String productName;
    private String image;
    @NotBlank
    @Size(min = 6, message = "Has to be more than 5 chars!!!")
    private String description;
    private Integer quantity;
    private double price;
    private double Discount;
    private double specialPrice;


}
