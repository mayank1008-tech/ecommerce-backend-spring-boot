package com.example.ecommerce.Services;

import com.example.ecommerce.payload.CartDTO;
import com.example.ecommerce.payload.CartReponse;
import jakarta.transaction.Transactional;

public interface CartService {
    CartDTO addProductToCart(Long productId, Integer quantity);

    CartReponse getallcarts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    CartDTO getCart(String emailId, Long id);

    @Transactional
    CartDTO updateProductQuantityInCart(Long productId, Integer quantity);

    CartDTO deleteCartProduct(Long cartId, Long productId);

    void updateProductInCarts(Long id, Long productId);

}
