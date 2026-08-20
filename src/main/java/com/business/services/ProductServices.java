package com.business.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.business.entities.Product;
import com.business.repositories.ProductRepository;

@Service
public class ProductServices 
{
	@Autowired
	private ProductRepository productRepository;

	//add Product
	public void addProduct(@NonNull Product p)
	{
		this.productRepository.save(p);
	}

	//getAll products
	public List<Product> getAllProducts()
	{
		List<Product> products = (List<Product>) this.productRepository.findAll();
		return products;
	}

	//get Single Product
	public Product getProduct(int id)
	{
		Optional<Product> optional = this.productRepository.findById(id);
		return optional.orElse(null);
	}

	//update Product
	public void updateproduct(@NonNull Product p, int id)
	{
		p.setPid(id);
		Optional<Product> optional = this.productRepository.findById(id);
		if (optional.isPresent())
		{
			this.productRepository.save(p);				
		}
	}
	
	//delete product
	public void deleteProduct(int id)
	{
		this.productRepository.deleteById(id);
	}

	//Get Product By Name
	public Product getProductByName(String name)
	{
		Product product = this.productRepository.findByPname(name);
		return product;
	}
}