package com.devsuperior.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devsuperior.dscatalog.domain.Product;
import com.devsuperior.dscatalog.domain.Role;
import com.devsuperior.dscatalog.domain.User;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	
	Role findByAuthority(String authority);
}
