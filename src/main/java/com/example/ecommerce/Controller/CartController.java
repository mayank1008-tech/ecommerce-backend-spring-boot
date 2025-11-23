package com.example.ecommerce.Controller;

import com.example.ecommerce.Exceptions.APIexception;
import com.example.ecommerce.Model.Cart;
import com.example.ecommerce.Repository.CartRepository;
import com.example.ecommerce.Services.CartService;
import com.example.ecommerce.Util.AuthUtil;
import com.example.ecommerce.config.AppConst;
import com.example.ecommerce.payload.CartDTO;
import com.example.ecommerce.payload.CartReponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class CartController {


    @Autowired
    CartService cartService;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    private CartRepository cartRepository;

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addToCart(@PathVariable Long productId,
                                             @PathVariable Integer quantity) {
        CartDTO newCartDTO = cartService.addProductToCart(productId, quantity);
        return new ResponseEntity<CartDTO>(newCartDTO, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/carts")
    public ResponseEntity<CartReponse> getAllCarts(@RequestParam(name="pageNumber", defaultValue = AppConst.PAGE_NUMBER, required = false) Integer pageNumber,
                                                   @RequestParam (name="pageSize", defaultValue = AppConst.PAGE_SIZE, required = false) Integer pageSize,
                                                   @RequestParam (name="sortBy", defaultValue = "totalPrice", required = false) String sortBy,
                                                   @RequestParam (name="sortOrder", defaultValue = AppConst.SORT_ORDER, required = false) String sortOrder) {
        CartReponse res = cartService.getallcarts(pageNumber, pageSize, sortBy,sortOrder);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getCartById(){
        String emailId = authUtil.loggedInEmail();
        Cart cart = cartRepository.findCartByEmail(emailId);
        if(cart==null){
            throw  new APIexception("No carts created till now!!!");
        }
        CartDTO cartDTO = cartService.getCart(emailId, cart.getId());
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateCartProductQuantity(@PathVariable Long productId,
                                                             @PathVariable String operation){
        CartDTO cartDTO = cartService.updateProductQuantityInCart(productId,
                operation.equalsIgnoreCase("delete")? -1:1);
        return new ResponseEntity<>(cartDTO,HttpStatus.OK);
    }

    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<CartDTO> deleteCartProduct(@PathVariable Long cartId,
                                                     @PathVariable Long productId){
        CartDTO cartDTO = cartService.deleteCartProduct(cartId, productId);
        return new ResponseEntity<>(cartDTO,HttpStatus.OK);
    }
}
