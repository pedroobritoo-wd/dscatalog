package com.devsuperior.dscatalog.dto;

import com.devsuperior.dscatalog.services.validation.UserInsertValid;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@UserInsertValid
public class UserInsertDTO extends UserDTO {

	private static final long serialVersionUID = 1L;
	
	@NotBlank(message = "Campo Obrigatório")
	@Size(min = 8, message = "Campo Obrigatório")
	private String password;

	UserInsertDTO(){
		super();
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
