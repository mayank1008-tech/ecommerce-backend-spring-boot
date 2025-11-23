package com.example.ecommerce.Repository;

import com.example.ecommerce.Model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id= ?1 AND ci.product.productId = ?2")
    CartItem findCartItemByProductIdAndCartId(Long cartId, Long productId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM CART_ITEM WHERE CART_ITEM_ID = ?1", nativeQuery = true)
    void deleteCartItemById(Long cartItemId);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id =?1 AND ci.product.productId =?2")
    void deleteCartItemByCartIdAndProductId(Long cartId, Long productId);
}
