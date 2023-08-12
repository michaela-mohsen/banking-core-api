package com.banking.springboot.controller;

import com.banking.springboot.dto.ProductDto;
import com.banking.springboot.service.impl.ProductServiceImpl;
import com.banking.springboot.util.Utility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ProductControllerTest {
    @InjectMocks
    private ProductController controller;

    @Mock
    private ProductServiceImpl service;

    @MockBean
    private Utility utility;

    @BeforeEach
    void init() {
        openMocks(this);
    }

    @Test
    void listAllProductsTest() {
        when(service.getAllProducts()).thenReturn(new ArrayList<>());
        Assertions.assertEquals(HttpStatus.OK, controller.listAllProducts().getStatusCode());
    }

    @Test
    void findProductByNameTest() {
        when(service.findProductByName(any())).thenReturn(new ProductDto());
        Assertions.assertEquals(HttpStatus.OK, controller.findProductByName("Auto Loan").getStatusCode());
    }
}
