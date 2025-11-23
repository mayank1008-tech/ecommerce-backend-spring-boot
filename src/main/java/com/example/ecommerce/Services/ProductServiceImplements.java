package com.example.ecommerce.Services;

import com.example.ecommerce.Exceptions.APIexception;
import com.example.ecommerce.Exceptions.ResourceNotFound;
import com.example.ecommerce.Model.Cart;
import com.example.ecommerce.Model.CartItem;
import com.example.ecommerce.Model.Category;
import com.example.ecommerce.Model.Product;
import com.example.ecommerce.Repository.CartItemRepository;
import com.example.ecommerce.Repository.CartRepository;
import com.example.ecommerce.Repository.CategoryRepository;
import com.example.ecommerce.Repository.ProductRepository;
import com.example.ecommerce.payload.CartDTO;
import com.example.ecommerce.payload.ProductDTO;
import com.example.ecommerce.payload.ProductResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImplements implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private FileService fileService;
    @Autowired
    private CartRepository  cartRepository;
    @Value("${project.image}")
    private String path;
    @Autowired
    private CartService cartService;
    @Autowired
    private CartItemRepository cartItemRepository;

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
        Product  product = modelMapper.map(productDTO, Product.class);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFound("Category ","Category Id",categoryId));
        product.setCategory(category);

        List<Product> products = category.getProducts();
        for (Product p : products) {
            if(p.getProductName().equals(productDTO.getProductName())) {
                throw new APIexception("Product already exists");
            }
        }

        product.setImage("default.png");
        double specialPrice = product.getPrice() - ((product.getDiscount()*0.01) * product.getPrice());
        product.setSpecialPrice(specialPrice);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {

        Sort sorted = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize,sorted);
        Page<Product> productPage = productRepository.findAll(pageable);

        List<Product> products = productPage.getContent();

        if(products.isEmpty()){
            throw new APIexception("No products added till now!!!");
        }

        List<ProductDTO> productDTOS= products.stream()
                .map(Product -> modelMapper.map(Product, ProductDTO.class))
                .toList();
        ProductResponse res =  new ProductResponse();
        res.setContent(productDTOS);

        res.setPageNumber(productPage.getNumber());
        res.setPageSize(productPage.getSize());
        res.setTotalElements(productPage.getTotalElements());
        res.setTotalPages(productPage.getTotalPages());
        res.setLastPage(productPage.isLast());

        return res;
    }

    @Override
    public ProductResponse getByCategory(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFound("Category ","Category Id",categoryId));

        Sort sorted = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize,sorted);
        Page<Product> productPage = productRepository.findByCategory(category,pageable);

        List<Product> products = productPage.getContent();

        if(products.isEmpty()){
            throw new APIexception("No products added till now!!!");
        }

        List<ProductDTO> productDTOS= products.stream()
                .map(Product -> modelMapper.map(Product, ProductDTO.class))
                .toList();
        ProductResponse res =  new ProductResponse();
        res.setContent(productDTOS);

        res.setPageNumber(productPage.getNumber());
        res.setPageSize(productPage.getSize());
        res.setTotalElements(productPage.getTotalElements());
        res.setTotalPages(productPage.getTotalPages());
        res.setLastPage(productPage.isLast());
        return res;
    }

    @Override
    public ProductResponse getByKeyword(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String keyword) {
        Sort sorted = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize,sorted);
        Page<Product> productPage = productRepository.findByProductNameLikeIgnoreCase('%'+keyword+'%', pageable);

        List<Product> products = productPage.getContent();

        if(products.isEmpty()){
            throw new APIexception("No such products!!!");
        }

        List<ProductDTO> productDTOS= products.stream()
                .map(Product -> modelMapper.map(Product, ProductDTO.class))
                .toList();
        ProductResponse res =  new ProductResponse();
        res.setContent(productDTOS);

        res.setPageNumber(productPage.getNumber());
        res.setPageSize(productPage.getSize());
        res.setTotalElements(productPage.getTotalElements());
        res.setTotalPages(productPage.getTotalPages());
        res.setLastPage(productPage.isLast());

        return res;
    }

    @Transactional
    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product product = modelMapper.map(productDTO, Product.class);
        Product oldProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFound("Product ","Product Id",productId));

        oldProduct.setProductName(product.getProductName());
        oldProduct.setCategory(product.getCategory());
        oldProduct.setPrice(product.getPrice());
        oldProduct.setQuantity(product.getQuantity());
        oldProduct.setDescription(product.getDescription());
        double specialPrice = product.getPrice() - ((product.getDiscount()*0.01) * product.getPrice());
        oldProduct.setSpecialPrice(specialPrice);
        oldProduct.setDiscount(product.getDiscount());

        productRepository.save(oldProduct);

        List<Cart> carts = cartRepository.findCartsByProductId(productId);

        List<CartDTO> cartDTOS = carts.stream().map(cart ->{
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
            List<ProductDTO> products = cart.getCartItems().stream()
                    .map(p -> modelMapper.map(p.getProduct(),ProductDTO.class))
                    .collect(Collectors.toList());
            cartDTO.setProductList(products);
            return cartDTO;
        }).collect(Collectors.toList());

        cartDTOS.forEach(cart -> cartService.updateProductInCarts(cart.getId(),productId));
        return modelMapper.map(oldProduct, ProductDTO.class);
    }

    @Transactional
    @Override
    public String deleteProduct(Long productId) {
        Product oldProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFound("Product ","Product Id",productId));

        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        carts.forEach(cart -> cartService.deleteCartProduct(cart.getId(), productId));

        // 2. CRITICAL FIX: Break the relationship in memory
        // This stops Hibernate from trying to cascade-delete the items we just deleted above.
        oldProduct.getCartItemList().clear();

        // 3. Delete Product
        productRepository.delete(oldProduct);

        return "Product has been deleted";
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFound("Product ","Product Id",productId));
        String filename = fileService.uploadImage(path, image);

        product.setImage(filename);
        productRepository.save(product);
        ProductDTO productDTO = modelMapper.map(product, ProductDTO.class);
        return productDTO;
    }


}
