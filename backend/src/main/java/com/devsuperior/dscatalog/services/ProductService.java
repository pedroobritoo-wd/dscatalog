package com.devsuperior.dscatalog.services;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.domain.Product;
import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {
	
	@Autowired
	private ProductRepository repository;
	
	@Transactional(readOnly = true)
	public List<ProductDTO> findAll(){
		List<Product> products = repository.findAll();
		List<ProductDTO> productsdtos = products.stream()
				.map(x -> new ProductDTO()).collect(Collectors.toList());
		return productsdtos;
	}
	
	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		Product pod = repository.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException("Entity not found"));
		ProductDTO podDTO = new ProductDTO(pod, pod.getCategories());
		return podDTO;
	}
	
	@Transactional
	public ProductDTO createProduct(ProductDTO cat) {
		Product product = new Product();
		product.setName(cat.getName());
		product.setDescription(cat.getDescription());
		product.setPrice(cat.getPrice());
		product.setImgUrl(cat.getImgUrl());
		product.setDate(Instant.now());
		product = repository.save(product);		
		
		return new ProductDTO(product);
		
	}
	
	@Transactional
	public ProductDTO updateProduct(ProductDTO cat, Long id) {
		Product product = repository.getReferenceById(id);
		product.setName(cat.getName());
		product.setDescription(cat.getDescription());
		product.setPrice(cat.getPrice());
		product.setImgUrl(cat.getImgUrl());
		product.setDate(Instant.now());
		
		return new ProductDTO(product);
		
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
	
	
	public Page<ProductDTO> findAllPaged(PageRequest pageRequest) {
		Page<Product> products = repository.findAll(pageRequest);
		Page<ProductDTO> productsdtos = products
				.map(x -> new ProductDTO(x));
		return productsdtos;
	}
	
	
}
