package com.devsuperior.dscatalog.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.domain.Role;
import com.devsuperior.dscatalog.domain.User;
import com.devsuperior.dscatalog.dto.RoleDTO;
import com.devsuperior.dscatalog.dto.UserDTO;
import com.devsuperior.dscatalog.dto.UserInsertDTO;
import com.devsuperior.dscatalog.dto.UserUpdateDTO;
import com.devsuperior.dscatalog.projections.UserDetailsProjection;
import com.devsuperior.dscatalog.repositories.RoleRepository;
import com.devsuperior.dscatalog.repositories.UserRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class UserService implements UserDetailsService {
	
	@Autowired
	private UserRepository repository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
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
		
		entity.getRoles().clear();
		Role role = roleRepository.findByAuthority("ROLE_OPERATOR");
		entity.addRole(role);
		
		entity.setPassword( passwordEncoder.encode(dto.getPassword()));
		entity = repository.save(entity);
		return new UserDTO(entity);	
		
	}

	@Transactional
	public UserDTO updateUser(UserUpdateDTO dto, Long id) {
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

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(username);
		if(result.size() == 0) {
			throw new UsernameNotFoundException("User not found");
		}
		
		User user = new User();
		user.setEmail(username);
		user.setPassword(result.get(0).getPassword());
		
		for(UserDetailsProjection projection : result) {
			user.addRole(new Role(projection.getRoleId(), projection.getAuthority() ));
		}
		
		return user;
	}
	
	
	
	
	
	
}
