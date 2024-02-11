package com.devsuperior.dscatalog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.domain.Role;
import com.devsuperior.dscatalog.domain.User;
import com.devsuperior.dscatalog.dto.RoleDTO;
import com.devsuperior.dscatalog.dto.UserDTO;
import com.devsuperior.dscatalog.dto.UserInsertDTO;
import com.devsuperior.dscatalog.repositories.RoleRepository;
import com.devsuperior.dscatalog.repositories.UserRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class UserService {
	
	@Autowired
	private UserRepository repository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	
	@Transactional(readOnly = true)
	public UserDTO findById(Long id) {
		User entity = repository.findById(id)
				.orElseThrow(()-> new ResourceNotFoundException("Entity not found"));
		return new UserDTO(entity);
		
	}
	
	@Transactional
	public UserDTO createUser(UserInsertDTO dto) {
		User entity = new User();
		copyDtoToEntity(entity, dto);
		entity.setPassword( passwordEncoder.encode(dto.getPassword()));
		entity = repository.save(entity);
		return new UserDTO(entity);	
		
	}

	@Transactional
	public UserDTO updateUser(UserDTO dto, Long id) {
		User entity = repository.getReferenceById(id);
		copyDtoToEntity(entity, dto);
		return new UserDTO(entity);
		
	}
	
	@Transactional(propagation = Propagation.SUPPORTS)
	public void deleteUser(Long id) {
		if(!repository.existsById(id)) {
			throw new ResourceNotFoundException("Recurso não encontrado");
		}
		try {
			repository.deleteById(id);
		} catch (DataIntegrityViolationException e) {
        	throw new DatabaseException("Falha de integridade referencial");
		}
	}
	
	
	public Page<UserDTO> findAllPaged(Pageable pageable) {
		Page<User> products = repository.findAll(pageable);
		Page<UserDTO> productsdtos = products
				.map(x -> new UserDTO(x));
		return productsdtos;
	}
	
	private void copyDtoToEntity(User entity, UserDTO dto) {
		entity.setFirstName(dto.getFirstName());
		entity.setLastName(dto.getLastName());
		entity.setEmail(dto.getEmail());
		entity.setId(dto.getId());
		
		entity.getRoles().clear();
		
		for(RoleDTO roleDto : dto.getRoles()) {
			Role role = roleRepository.getReferenceById(roleDto.getId());
			entity.getRoles().add(role);
		}
	}
	
	
	
	
	
	
}
