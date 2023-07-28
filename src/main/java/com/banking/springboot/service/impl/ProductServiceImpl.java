package com.banking.springboot.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.banking.springboot.dto.ProductDto;
import com.banking.springboot.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.banking.springboot.repository.ProductRepository;
import com.banking.springboot.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;
	
	public ProductServiceImpl(ProductRepository productRepository) {
		super();
		this.productRepository = productRepository;
	}

	@Override
	public List<ProductDto> getAllProducts() {
		List<Product> products = productRepository.findAll();
		List<ProductDto> productsToJson = new ArrayList<>();
		for(Product p : products) {
			ProductDto dto = new ProductDto();
			dto.setId(p.getId());
			dto.setName(p.getName());
			dto.setType(p.getType());
			productsToJson.add(dto);
		}
		return productsToJson;
	}

	@Override
	public ProductDto findProductByName(String name) {
		Product product = productRepository.findByName(name);
		ProductDto dto = new ProductDto();
		dto.setId(product.getId());
		dto.setName(product.getName());
		dto.setType(product.getType());
		return dto;
	}


}
