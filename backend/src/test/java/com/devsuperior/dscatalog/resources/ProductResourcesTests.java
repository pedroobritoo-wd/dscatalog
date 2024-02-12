package com.devsuperior.dscatalog.resources;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;


@WebMvcTest(value = ProductResource.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class ProductResourcesTests {

	@Autowired
	private MockMvc mockMvc;

	
	@MockBean
	private ProductService service;
	
	private PageImpl<ProductDTO> page;
	private ProductDTO productDTO;
	private Long realId;
	private Long badId;
	private Long dependetId;
	
	@BeforeEach
	void setUp() throws Exception{
		
		realId =1l;
		badId =2l;
		dependetId = 3l;
		
		productDTO = Factory.createProductDTO();		
		page = new PageImpl<>(List.of(productDTO));		
		when(service.findAllPaged(any())).thenReturn(page);
		
		when(service.findById(realId)).thenReturn(productDTO);
		when(service.findById(badId)).thenThrow(ResourceNotFoundException.class);
		
		when(service.updateProduct(productDTO, realId)).thenReturn(productDTO);
		when(service.updateProduct(productDTO, badId)).thenThrow(ResourceNotFoundException.class);
		
		doNothing().when(service).deleteProduct(realId);
		doThrow(ResourceNotFoundException.class).when(service).deleteProduct(badId);
		doThrow(DatabaseException.class).when(service).deleteProduct(dependetId);
	}
	
	@Test
	public void findAllShouldReturnPage() throws Exception {
		mockMvc.perform(get("/products").accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}
	
	@Test
	public void findByIdShouldReturnProductWhenIdExists() throws Exception {
		mockMvc.perform(get("/products/{id}", realId).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk()).andExpect(jsonPath("$.id").exists());
	}
	
	@Test
	public void findByIdShouldNotFoundIdDoNotExists() throws Exception {
		mockMvc.perform(get("/products/{id}", badId).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound());
	}
	
	@Test
	public void doNothingWhenDeletingWithValidId() throws Exception {
		mockMvc.perform(delete("/products/{id}", realId).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNoContent());
	}
	
	@Test
	public void throwExceptionWhenDeletingWithInvalidId() throws Exception {
		mockMvc.perform(delete("/products/{id}", badId).accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound());
	}
	
	/**
	@Test
	public void updateShouldReturnProductWhenIdExists() throws Exception {
		
		String jsonBody = objMapper.writeValueAsString(productDTO);
		
		mockMvc.perform(put("/products/{id}", realId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk());
	}
	
	@Test
	public void updateShouldShouldNotFoundWhenIdDoNotExists() throws Exception{
	String jsonBody = objMapper.writeValueAsString(productDTO);
		
		mockMvc.perform(put("/products/{id}", badId)
				.content(jsonBody)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
		.andExpect(status().isNotFound());
	}
	**/
	
	
}
