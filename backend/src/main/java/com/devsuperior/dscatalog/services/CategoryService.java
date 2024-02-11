package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.domain.Category;
import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {
	
	@Autowired
	private CategoryRepository repository;
	
	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		Category cat = repository.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException("Entity not found"));
		return new CategoryDTO(cat);
	}
	
	@Transactional
	public CategoryDTO createCategory(CategoryDTO cat) {
		Category category = new Category();
		category.setName(cat.getName());
		category = repository.save(category);
		
		return new CategoryDTO(category);
		
	}
	
	@Transactional
	public CategoryDTO updateCategory(CategoryDTO cat, Long id) {
		Category category = repository.getReferenceById(id);
		category.setName(cat.getName());
		category = repository.save(category);
		
		return new CategoryDTO(category);
		
	}
	
	@Transactional(propagation = Propagation.SUPPORTS)
	public void deleteCategory(Long id) {
		if(!repository.existsById(id)) {
			throw new ResourceNotFoundException("Recurso não encontrado");
		}
		
		try {
			repository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
        	throw new DatabaseException("Falha de integridade referencial");
   	}
		
	}
	
	
	@Transactional(readOnly = true)
	public Page<CategoryDTO> findAllPaged(Pageable pageable) {
		Page<Category> list = repository.findAll(pageable);
		return list.map(x -> new CategoryDTO(x));
	}
	
	
}
