package com.banking.springboot.service.impl;

import com.banking.springboot.dto.ProductDto;
import com.banking.springboot.entity.Product;
import com.banking.springboot.repository.ProductRepository;
import com.banking.springboot.util.Utility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class ProductServiceImplTest {
    @Autowired
    private ProductServiceImpl service;

    @MockBean
    private ProductRepository repository;

    @MockBean
    private Utility utility;

    @Test
    void getAllProductsTest() {
        Product product = new Product();
        List<Product> products = new ArrayList<>();
        products.add(product);
        when(repository.findAll()).thenReturn(products);
        when(utility.convertProductToJson(any())).thenReturn(new ProductDto());
        Assertions.assertEquals(1, service.getAllProducts().size());
    }

    @Test
    void findProductByNameTest() {
        when(repository.findByName(any())).thenReturn(new Product());
        when(utility.convertProductToJson(any())).thenReturn(new ProductDto());
        Assertions.assertNotNull(service.findProductByName("Product"));
    }
}
