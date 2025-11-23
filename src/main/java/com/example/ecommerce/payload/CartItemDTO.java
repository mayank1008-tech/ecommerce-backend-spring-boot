package com.example.ecommerce.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    private Long id;
    private CartDTO cart;
    private  ProductDTO product;
    private Double discount;
    private Integer quantity;
    private double price;
}
