package com.devsuperior.dscatalog.infra;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.devsuperior.dscatalog.resources.exceptions.StandardError;
import com.devsuperior.dscatalog.services.exceptions.EntityNotFoundException;

@RestControllerAdvice
public class ControllerExceptionHandler {

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<StandardError> EntityNotFound(EntityNotFoundException e){
		StandardError error = new StandardError();
		error.setTimestamp(Instant.now());
		error.setStatus(HttpStatus.NOT_FOUND.value());
		error.setError("Resource not found");
		error.setMessage(e.getMessage());
		error.setPath(null);
		
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
}
