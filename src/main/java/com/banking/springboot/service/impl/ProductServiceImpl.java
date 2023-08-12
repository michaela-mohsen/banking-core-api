package com.banking.springboot.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.banking.springboot.dto.ProductDto;
import com.banking.springboot.entity.Product;
import com.banking.springboot.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.banking.springboot.repository.ProductRepository;
import com.banking.springboot.service.ProductService;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private Utility util;

	@Override
	public List<ProductDto> getAllProducts() {
		log.info("Inside getAllProducts()");
		List<Product> products = productRepository.findAll();
		List<ProductDto> productsToJson = new ArrayList<>();
		for(Product p : products) {
			ProductDto dto = util.convertProductToJson(p);
			productsToJson.add(dto);
		}
		return productsToJson;
	}

	@Override
	public ProductDto findProductByName(String name) {
		log.info("Inside findProductByName: {}", name);
		Product product = productRepository.findByName(name);
		return util.convertProductToJson(product);
	}


}
