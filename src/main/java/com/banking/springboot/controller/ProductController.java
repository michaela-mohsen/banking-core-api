package com.banking.springboot.controller;

import com.banking.springboot.dto.ProductDto;
import com.banking.springboot.service.impl.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping
@CrossOrigin
@RestController
public class ProductController {
    @Autowired
    private ProductServiceImpl productService;

    @GetMapping("/products")
    public ResponseEntity<Object> listAllProducts() {
        List<ProductDto> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/products/search")
    public ResponseEntity<Object> findProductByName(@RequestParam String name) {
        ProductDto dto = productService.findProductByName(name);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}
