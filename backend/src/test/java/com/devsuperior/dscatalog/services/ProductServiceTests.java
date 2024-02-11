package com.devsuperior.dscatalog.services;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.domain.Product;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
	
	private Long productId;
	private Long nonProductId;
	private Long dependentId;
	private Product product;
	private PageImpl<Product> page;
	
	@BeforeEach
	void setUp() throws Exception{
		productId = 1l;
		nonProductId = 110l;
		dependentId = 2l;
		product = Factory.createProduct();
		page = new PageImpl<>(List.of(product));
		
		doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
		
		when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		
		when(repository.findById(productId)).thenReturn(Optional.of(product));
		when(repository.findById(nonProductId)).thenReturn(Optional.empty());
		
		when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
		
		when(repository.getReferenceById(productId)).thenReturn(product);
		when(repository.getReferenceById(nonProductId)).thenThrow(EntityNotFoundException.class);
		
		when(repository.existsById(productId)).thenReturn(true);
		when(repository.existsById(nonProductId)).thenReturn(false);
		when(repository.existsById(dependentId)).thenReturn(true);

	}
	
	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		
		Assertions.assertDoesNotThrow(()->{
			service.deleteProduct(productId);
		});
		
		verify(repository, times(1)).deleteById(productId);
	}
	
	@Test
	public void deleteShouldThrowExceptionWhenIdExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, ()->{
			service.deleteProduct(nonProductId);
		});
		
		verify(repository, times(0)).deleteById(nonProductId);
	}
	
	@Test
	public void deleteShouldThrowExceptionDatabaseExceptionWhenIdDependent() {
		
		Assertions.assertThrows(DatabaseException.class, ()->{
			service.deleteProduct(dependentId);
		});
		
		verify(repository, times(1)).deleteById(dependentId);
	}
	
	@Test
	public void findAllPageShouldReturnPage() {
		
		Pageable pages = PageRequest.of(0, 10);
		Page<ProductDTO> result = service.findAllPaged(pages);
		
		Assertions.assertFalse(result.isEmpty());
		verify(repository, times(1)).findAll(pages);
	}
	
	@Test
	public void findByIdShouldReturnProductDTOWhenIdExist() {
		
		ProductDTO product = service.findById(productId);
		
		Assertions.assertNotNull(product);
		verify(repository, times(1)).findById(productId);
	}
	
	@Test
	public void findByIdShouldReturnProductDTOWhenIdDoNotExist() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, ()->{
			service.findById(nonProductId);
		});
		
		verify(repository, times(1)).findById(nonProductId);
	}
	
	@Test
	public void updateProductIdShouldReturnProductDTOWhenIdExist() {
		ProductDTO product = new ProductDTO();
		product = service.updateProduct(product, productId);
		
		Assertions.assertNotNull(product);
		verify(repository, times(1)).getReferenceById(productId);
	}
	
	@Test
	public void updateProductIdShouldThrowExceptionEntityNotFoundExceptionWhenIdDoNotExist() {
		
		Assertions.assertThrows(EntityNotFoundException.class, ()->{
			ProductDTO product = new ProductDTO();
			product = service.updateProduct(product, nonProductId);
		});
		
		verify(repository, times(1)).getReferenceById(nonProductId);
	}
	
	

}
