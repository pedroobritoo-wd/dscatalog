package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
	public List<CategoryDTO> findAll(){
		List<Category> categories = repository.findAll();
		List<CategoryDTO> categoriesdtos = categories.stream()
				.map(x -> new CategoryDTO(x.getName(), x.getId())).collect(Collectors.toList());
		return categoriesdtos;
	}
	
	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		Category cat = repository.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException("Entity not found"));
		CategoryDTO catDTO = new CategoryDTO(cat.getName(), cat.getId());
		return catDTO;
	}
	
	@Transactional
	public CategoryDTO createCategory(CategoryDTO cat) {
		Category category = new Category();
		category.setName(cat.name());
		category = repository.save(category);
		
		return new CategoryDTO(category.getName(), category.getId());
		
	}
	
	@Transactional
	public CategoryDTO updateCategory(CategoryDTO cat, Long id) {
		Category category = repository.getReferenceById(id);
		category.setName(cat.name());
		category = repository.save(category);
		
		return new CategoryDTO(category.getName(), id);
		
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
	
	
	public Page<CategoryDTO> findAllPaged(PageRequest pageRequest) {
		Page<Category> categories = repository.findAll(pageRequest);
		Page<CategoryDTO> categoriesdtos = categories
				.map(x -> new CategoryDTO(x.getName(), x.getId()));
		return categoriesdtos;
	}
	
	
}
