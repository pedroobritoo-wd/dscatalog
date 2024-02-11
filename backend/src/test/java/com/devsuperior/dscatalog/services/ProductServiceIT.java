package com.devsuperior.dscatalog.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
@Transactional
public class ProductServiceIT {
	
	@Autowired
	private ProductService productService;
	
	
	private Long existingId;
	private Long nonExistingId;
	private int countTotalProducts;
	
	@BeforeEach
	void setUp() throws Exception{
		existingId = 1l;
		nonExistingId = 1000l;
		countTotalProducts = 25;
	}
	
	@Test
	public void deleteShouldDeleteWhenIdExist() {
		productService.deleteProduct(existingId);
		
		Assertions.assertThrows(ResourceNotFoundException.class, ()->{
			productService.findById(existingId);
		});
	}
	
	@Test
	public void deleteShouldThrowExceptionWhenIdDoNotExist() {		
		Assertions.assertThrows(ResourceNotFoundException.class, ()->{
			productService.deleteProduct(nonExistingId);
		});
	}
	
	@Test
	public void findAllPagedShouldReturnPaged() {
		PageRequest page = PageRequest.of(0, 10);
		Page<ProductDTO> pages = productService.findAllPaged(page);
		
		Assertions.assertFalse(pages.isEmpty());
		Assertions.assertEquals(pages.getSize(), 10);
		Assertions.assertEquals(pages.getNumber(), 0);
		Assertions.assertEquals(pages.getTotalElements(), 25);
	}
	
	@Test
	public void findAllPagedShouldReturnNothingInPage50() {
		PageRequest page = PageRequest.of(50, 10);
		Page<ProductDTO> pages = productService.findAllPaged(page);
		
		Assertions.assertTrue(pages.isEmpty());
	
	}
}
