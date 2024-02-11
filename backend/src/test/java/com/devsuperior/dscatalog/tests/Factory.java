package com.devsuperior.dscatalog.tests;

import java.time.Instant;

import com.devsuperior.dscatalog.domain.Category;
import com.devsuperior.dscatalog.domain.Product;
import com.devsuperior.dscatalog.dto.ProductDTO;

public class Factory {
	
	//Long id, String name, String description, Double price, String imgUrl, Instant date
	public static Product createProduct() {
		Product product = new Product(1l, "PS5", "Video Game Console", 500d, "null", Instant.now() );
		product.getCategories().add(new Category(3l, "Eletrônicos"));
		return product;
	}
	
	public static ProductDTO createProductDTO() {
		Product product = createProduct();
		return new ProductDTO(product, product.getCategories());
	}
	
}
