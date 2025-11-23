package com.example.ecommerce.Controller;

import com.example.ecommerce.Model.Product;
import com.example.ecommerce.Services.ProductService;
import com.example.ecommerce.config.AppConst;
import com.example.ecommerce.payload.ProductDTO;
import com.example.ecommerce.payload.ProductResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/admin/categories/{CategoryId}/product")
    public ResponseEntity<ProductDTO> addProduct( @PathVariable("CategoryId") Long CategoryId,@Valid @RequestBody ProductDTO productDTO) {
        ProductDTO resProductDTO = productService.addProduct(productDTO, CategoryId);
        return new ResponseEntity<>(resProductDTO ,HttpStatus.CREATED);
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(@RequestParam(name="pageNumber", defaultValue = AppConst.PAGE_NUMBER, required = false) Integer pageNumber,
                                                          @RequestParam (name="pageSize", defaultValue = AppConst.PAGE_SIZE, required = false) Integer pageSize,
                                                          @RequestParam (name="sortBy", defaultValue = "price", required = false) String sortBy,
                                                          @RequestParam (name="sortOrder", defaultValue = AppConst.SORT_ORDER, required = false) String sortOrder) {
        ProductResponse productResponse = productService.getAllProducts(pageNumber,pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }

    @GetMapping("/public/categories/{CategoryId}/products")
    public ResponseEntity<ProductResponse> getByCategory(@RequestParam(name="pageNumber", defaultValue = AppConst.PAGE_NUMBER, required = false) Integer pageNumber,
                                                         @RequestParam (name="pageSize", defaultValue = AppConst.PAGE_SIZE, required = false) Integer pageSize,
                                                         @RequestParam (name="sortBy", defaultValue = "price", required = false) String sortBy,
                                                         @RequestParam (name="sortOrder", defaultValue = AppConst.SORT_ORDER, required = false) String sortOrder,
                                                         @PathVariable("CategoryId") Long  CategoryId) {
        ProductResponse productResponse = productService.getByCategory(pageNumber,pageSize, sortBy, sortOrder, CategoryId);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);

    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getByKeyword(@RequestParam(name="pageNumber", defaultValue = AppConst.PAGE_NUMBER, required = false) Integer pageNumber,
                                                        @RequestParam (name="pageSize", defaultValue = AppConst.PAGE_SIZE, required = false) Integer pageSize,
                                                        @RequestParam (name="sortBy", defaultValue = "price", required = false) String sortBy,
                                                        @RequestParam (name="sortOrder", defaultValue = AppConst.SORT_ORDER, required = false) String sortOrder,
                                                        @PathVariable("keyword") String keyword){
        ProductResponse productResponse = productService.getByKeyword(pageNumber,pageSize, sortBy, sortOrder, keyword);
        return new ResponseEntity<>(productResponse, HttpStatus.FOUND);
    }

    @PutMapping("/admin/products/{ProductId}")
    public ResponseEntity<ProductDTO> updateProduct( @PathVariable("ProductId") Long ProductId,@Valid @RequestBody ProductDTO productDTO) {
        ProductDTO resProductDTO = productService.updateProduct(ProductId, productDTO);
        return new ResponseEntity<>(resProductDTO, HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/{ProductId}")
    public ResponseEntity<String> deleteProduct(@PathVariable("ProductId") Long ProductId) {
        String status = productService.deleteProduct(ProductId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

    @PutMapping("/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateImage(@PathVariable("productId") Long productId,
                                                  @RequestParam("image") MultipartFile image) throws IOException {
        ProductDTO resProductDTO = productService.updateProductImage(productId, image);
        return new ResponseEntity<>(resProductDTO, HttpStatus.OK);
    }



}
