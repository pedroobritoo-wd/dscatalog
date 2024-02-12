package com.devsuperior.dscatalog.services;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.domain.Category;
import com.devsuperior.dscatalog.domain.Product;
import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.projections.ProductProjection;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {
	
	@Autowired
	private ProductRepository repository;
	
	@Autowired
	private CategoryRepository catRepo;
	
	
	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		Product pod = repository.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException("Entity not found"));
		ProductDTO podDTO = new ProductDTO(pod, pod.getCategories());
		return podDTO;
	}
	
	@Transactional
	public ProductDTO createProduct(ProductDTO dto) {
		Product entity = new Product();
		copyDtoToEntity(dto, entity);
		entity = repository.save(entity);
		return new ProductDTO(entity);	
		
	}
	
	@Transactional
	public ProductDTO updateProduct(ProductDTO dto, Long id) {
		Product entity = repository.getReferenceById(id);
		copyDtoToEntity(dto, entity);
		
		return new ProductDTO(entity, entity.getCategories());
		
	}
	
	@Transactional(propagation = Propagation.SUPPORTS)
	public void deleteProduct(Long id) {
		if(!repository.existsById(id)) {
			throw new ResourceNotFoundException("Recurso não encontrado");
		}
		try {
			repository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
        	throw new DatabaseException("Falha de integridade referencial");
   	}
		
	}
	
	
	public Page<ProductDTO> findAllPaged(Pageable pageable) {
		Page<Product> products = repository.findAll(pageable);
		Page<ProductDTO> productsdtos = products
				.map(x -> new ProductDTO(x));
		return productsdtos;
	}
	
	private void copyDtoToEntity(ProductDTO dto, Product entity) {

		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setDate(dto.getDate());
		entity.setImgUrl(dto.getImgUrl());
		entity.setPrice(dto.getPrice());
		
		entity.getCategories().clear();
		
		for (CategoryDTO catDto : dto.getCategories()) {
			Category category = catRepo.getReferenceById(catDto.getId());
			entity.getCategories().add(category);			
		}
	}
	
	@Transactional(readOnly = true)
	public Page<ProductDTO> findAllPaged(String name, String categoryId, Pageable pageable) {
		
		List<Long> ids = Arrays.asList();
		if(!"0".equals(categoryId)) {
			ids = Arrays.asList(categoryId.split(",")).stream().map(x -> Long.parseLong(x)).toList();
		}
		
		Page<ProductProjection> page = repository.searchProducts(ids, name, pageable);
		List<Long> productsId = page.map(x -> x.getId()).toList();
		
		List<Product> entities = repository.searchProductsWithCategories(productsId);
		
		List<ProductDTO> dtos = entities.stream().map(p -> new ProductDTO(p, p.getCategories())).toList();
		
		Page<ProductDTO> pageDto = new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
		
		return pageDto;
		
	}	
	
	
}
