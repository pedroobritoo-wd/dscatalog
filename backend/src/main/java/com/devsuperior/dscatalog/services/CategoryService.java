package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.domain.Category;
import com.devsuperior.dscatalog.dto.CategoryDTO;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.EntityNotFoundException;

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
				.orElseThrow(()-> new EntityNotFoundException("Entity not found"));
		CategoryDTO catDTO = new CategoryDTO(cat.getName(), cat.getId());
		return catDTO;
	}

	public CategoryDTO createCategory(CategoryDTO cat) {
		Category category = new Category();
		category.setName(cat.name());
		category = repository.save(category);
		
		return new CategoryDTO(category.getName(), category.getId());
		
	}
	
	
}
