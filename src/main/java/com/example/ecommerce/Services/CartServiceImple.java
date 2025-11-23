package com.example.ecommerce.Services;

import com.example.ecommerce.Exceptions.APIexception;
import com.example.ecommerce.Exceptions.ResourceNotFound;
import com.example.ecommerce.Model.Cart;
import com.example.ecommerce.Model.CartItem;
import com.example.ecommerce.Model.Product;
import com.example.ecommerce.Repository.CartItemRepository;
import com.example.ecommerce.Repository.CartRepository;
import com.example.ecommerce.Repository.ProductRepository;
import com.example.ecommerce.Util.AuthUtil;
import com.example.ecommerce.payload.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImple implements CartService{

    @Autowired
     CartRepository cartRepository;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {

        //Creating new or retrieving existing users cart
        Cart cart = createCart();

        //Checking is provided Product exists or not
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFound("Product","productId",productId));

        //Getting cartItem to check if it is already in the cart or not
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(
                cart.getId(),
                productId);
        if(cartItem != null) {
            throw new APIexception("Product "+product.getProductName()+" already exists in cart");
        }

        //Check if the provided quantity is available or not
        if(product.getQuantity() < quantity) {
            throw new APIexception("Product "+product.getProductName()+" quantity less than "+quantity);
        }

        //Check if it is out of stock
        if(product.getQuantity() ==0) {
            throw new APIexception("Product "+product.getProductName()+"  is not available");
        }

        //Saving up the new Cart item
        CartItem newCartItem = new CartItem();
        newCartItem.setCart(cart);
        newCartItem.setProduct(product);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setSpecialPrice(product.getSpecialPrice());

        cartItemRepository.save(newCartItem);

        cart.getCartItems().add(newCartItem); //To defeat the stale state(Purani loaded cart mai item add kar diya)

        product.setQuantity(product.getQuantity());//Leaving for now
        productRepository.save(product);

        //Updating cart
        cart.setTotalPrice(cart.getTotalPrice() + product.getSpecialPrice()*quantity);
        cartRepository.save(cart);

        //Creating the cartDTO
        CartDTO cartDTO = modelMapper.map(cart,CartDTO.class);


        //Getting List<CartItems> from Cart
        List<CartItem> cartItemList = cart.getCartItems();
        //Now for every cartItem retrieve it ProductDTO
        Stream<ProductDTO> productDTOStream = cartItemList.stream().map(item -> {
                    ProductDTO productDTO = modelMapper.map(item.getProduct(), ProductDTO.class);
//******IMP*********Now setting quantity(kitne cartItem hai not actual product's quantity) for each productDTO********IMP********
                    productDTO.setQuantity(item.getQuantity());
                    return productDTO;
                });


        //Saving the productDTOs explicitly
        cartDTO.setProductList(productDTOStream.toList());

        return cartDTO;
    }

    private Cart createCart(){
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(userCart != null){
            return userCart;
        }
        Cart newCart = new Cart();
        newCart.setTotalPrice(0.00);
        newCart.setUser(authUtil.loggedInUser());
        cartRepository.save(newCart);
        return newCart;
    }

    @Override
    public CartReponse getallcarts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder){

        Sort sorted = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pagedetails = PageRequest.of(pageNumber,pageSize, sorted);
        Page<Cart> cartPage = cartRepository.findAll(pagedetails);
        List<Cart> carts = cartPage.getContent();

        if(carts.isEmpty()){
            throw new APIexception("No cart created till now!!!");
        }

//******IMP*********Now setting every cart to cartDTO but CartDTO has List<ProductDTO> BUT CART doesn't have it so we will take it
        //          from List<CartItems> which is present in Cart              ********IMP***************************************
        List<CartDTO> cartDTOList= carts.stream()
                .map(cart -> {
                CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
                    // 2. Get the list of items
                    // (This works because you have @Transactional on the method)
                    List<CartItem> cartItemList = cart.getCartItems();

                    // 3. Manually map the List<CartItem> to a List<ProductDTO>
                    List<ProductDTO> productDTOs = cartItemList.stream().map(cartItem -> {

                        // A. Get the Product object *from* the CartItem
                        Product product = cartItem.getProduct();

                        // B. Map that Product object (not the CartItem) to a ProductDTO
                        ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);

                        // C. **CRITICAL:** Set the quantity from the CartItem,
                        //    not the product's total stock.
                        productDTO.setQuantity(cartItem.getQuantity());

                        return productDTO;

                    }).toList();
                cartDTO.setProductList(productDTOs);
                return cartDTO;
                }).toList();

        CartReponse res = new CartReponse();
        res.setCartDTOList(cartDTOList);

        //To set JSON metdata
        res.setPageNumber(cartPage.getNumber());
        res.setPageSize(cartPage.getSize());
        res.setTotalElements(cartPage.getTotalElements());
        res.setTotalPages(cartPage.getTotalPages());
        res.setLastPage(cartPage.isLast());
        return res;
    }

    @Override
    public CartDTO getCart(String emailId, Long id) {
        Cart cart = cartRepository.findCartByEmailandId(emailId, id);
        if(cart==null){
            throw new ResourceNotFound("Cart","CartId",id);
        }
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        // 2. Get the list of items
        // (This works because you have @Transactional on the method)
        List<CartItem> cartItemList = cart.getCartItems();

        // 3. Manually map the List<CartItem> to a List<ProductDTO>
        List<ProductDTO> productDTOs = cartItemList.stream().map(cartItem -> {

            // A. Get the Product object *from* the CartItem
            Product product = cartItem.getProduct();

            // B. Map that Product object (not the CartItem) to a ProductDTO
            ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);

            // C. **CRITICAL:** Set the quantity from the CartItem,
            //    not the product's total stock.
            productDTO.setQuantity(cartItem.getQuantity());

            return productDTO;

        }).toList();
        cartDTO.setProductList(productDTOs);

        return  cartDTO;
    }

    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
        String Email = authUtil.loggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(Email);
        if(userCart == null) throw new APIexception("No cart created till now!!!");

        Long id = userCart.getId();
        Cart cart = cartRepository.findCartById(id);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFound("Product","productId",productId));

        // Validation logic...
        if (quantity > 0 && product.getQuantity() < quantity) {
            throw new APIexception("Stock limit exceeded");
        }

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new APIexception("Product " + product.getProductName() + " does not exist in cart"));

        // Calculate New State
        int newQuantity = cartItem.getQuantity() + quantity;

        if (newQuantity <= 0) {
            // --- DELETE PATH ---

            // 1. Update Price First
            cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getSpecialPrice() * cartItem.getQuantity()));

            // 2. Save the Cart Price Change
            cartRepository.save(cart);

            // 3. HARD DELETE THE ITEM (Bypassing Hibernate List Logic)
            cartItemRepository.deleteCartItemById(cartItem.getCartItemId());

            // 4. RE-FETCH CART (Crucial!)
            // We fetch the cart again to get the clean state (Price updated + Item gone)
            // This ensures the DTO doesn't have the ghost item.
            cart.getCartItems().removeIf(item -> item.getCartItemId().equals(cartItem.getCartItemId()));

        } else {
            // --- UPDATE PATH ---
            cartItem.setSpecialPrice(product.getSpecialPrice());
            cartItem.setQuantity(newQuantity);
            cartItem.setDiscount(product.getDiscount());

            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getSpecialPrice() * quantity));

            cartRepository.save(cart);

        }

        // 4. Map and Return
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<ProductDTO> productDTOs = cart.getCartItems().stream().map(crtitm -> {
            ProductDTO productDTO = modelMapper.map(crtitm.getProduct(), ProductDTO.class);
            productDTO.setQuantity(crtitm.getQuantity());
            return productDTO;
        }).toList();
        cartDTO.setProductList(productDTOs);

        return cartDTO;
    }

    @Transactional
    @Override
    public CartDTO deleteCartProduct(Long cartId, Long productId) {

        Cart cart =  cartRepository.findCartById(cartId);
        if(cart == null) {
            throw new APIexception("Cart doesnt exists");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFound("Product","productId",productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if(cartItem == null) {
            throw new APIexception("Product "+product.getProductName()+" does not exist in cart");
        }

        cartItemRepository.deleteCartItemByCartIdAndProductId(cartId,productId);

        //Getting freshCart to defeat Stale state
        Cart freshCart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFound("Cart", "id", cartId));
        //Getting new Total
        double newTotal = freshCart.getCartItems().stream()
                .mapToDouble(item -> item.getSpecialPrice() * item.getQuantity())
                .sum();
        //Setting new sum
        freshCart.setTotalPrice(newTotal);

        CartDTO cartDTO = modelMapper.map(freshCart, CartDTO.class);
        List<CartItem> cartItemList = freshCart.getCartItems();

        // 3. Manually map the List<CartItem> to a List<ProductDTO>
        List<ProductDTO> productDTOs = cartItemList.stream().map(crtitm -> {
            Product producttemp = crtitm.getProduct();
            ProductDTO productDTO = modelMapper.map(producttemp, ProductDTO.class);
            productDTO.setQuantity(crtitm.getQuantity());
            return productDTO;
        }).toList();
        cartDTO.setProductList(productDTOs);

        return  cartDTO;
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        Cart cart =  cartRepository.findCartById(cartId);
        if(cart == null) {
            throw new APIexception("Cart doesnt exists");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFound("Product","productId",productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if(cartItem == null) {
            throw new APIexception("Product "+product.getProductName()+" does not exist in cart");
        }

        double CartTotalAfterRemovingCartItems =  cart.getTotalPrice() -
                (cartItem.getSpecialPrice() * cartItem.getQuantity());

        cartItem.setSpecialPrice(product.getSpecialPrice()); //Updating the cartItems price

        cart.setTotalPrice(CartTotalAfterRemovingCartItems + //Adding new price of the cartItems
                (cartItem.getSpecialPrice() * cartItem.getQuantity()));

        cartItemRepository.save(cartItem);
    }
}
