package com.banking.springboot.service;

import com.banking.springboot.dto.ProductDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ProductService {
	List<ProductDto> getAllProducts();
	ProductDto findProductByName(String name);
}
