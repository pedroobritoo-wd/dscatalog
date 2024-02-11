package com.devsuperior.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.devsuperior.dscatalog.domain.Product;
import com.devsuperior.dscatalog.tests.Factory;

@DataJpaTest
public class ProductRepositoryTests {
	
	Long productId;
	Long nonProductId;
	Long totalProducts;
	
	@BeforeEach
	void setUp() throws Exception{
		productId = 1l;
		nonProductId = 110l;
		totalProducts = 25l;
	}
	
	@Autowired
	private ProductRepository repository;
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {
		
		repository.deleteById(productId);
		Optional<Product> result =  repository.findById(productId);
		
		Assertions.assertFalse(result.isPresent());
	}
	
	@Test
	public void saveShouldPersistWithAutoIncrement() {
		
		Product product = Factory.createProduct();
		product.setId(null);
		product = repository.save(product);
		
		
		Assertions.assertNotNull(product);
		Assertions.assertEquals(totalProducts + 1, product.getId());
	}
	
	@Test
	public void findByIdshouldReturnEmptyWhenProductIdNotExists() {
		
		Optional<Product> product = repository.findById(totalProducts+1);		
		Assertions.assertFalse(product.isPresent());
	}
	
	@Test
	public void findByIdshouldReturnProductWhenProductIdExists() {
		
		Optional<Product> product = repository.findById(productId);		
		Assertions.assertTrue(product.isPresent());
	}
	
	
	
	
}
