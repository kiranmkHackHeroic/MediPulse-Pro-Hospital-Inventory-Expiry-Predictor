package com.business.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.business.entities.Product;
import com.business.entities.User;
import com.business.loginCredentials.AdminLogin;
import com.business.services.ProductServices;
import com.business.services.UserServices;

@Controller
public class HomeController 
{
	@Autowired
	private ProductServices productServices;
	@Autowired
	private UserServices userServices;
	@GetMapping({"/", "/home"})
	public String home()
	{
		return "Home";
	}

	@GetMapping("/products")
	public String products( Model model)
	{ 
		List<Product> allProducts = this.productServices.getAllProducts();
		model.addAttribute("products", allProducts);
		return "Products";
	}

	@GetMapping("/location")
	public String location()
	{
		return "Locate_us";
	}

	@GetMapping("/about")
	public String about()
	{
		return "About";
	}

	@GetMapping("/login")
	public String login(Model model)
	{
		model.addAttribute("adminLogin",new AdminLogin());
		return "Login";
	}
	@GetMapping("/register")
	public String register()
	{
		return "Register";
	}

	@PostMapping("/registerUser")
	public String registerUser(@ModelAttribute User user, Model model)
	{
		// Check if email already exists
		User existing = this.productServices != null ? null : null;
		try {
			existing = this.userServices.getUserByEmail(user.getUemail());
		} catch (Exception e) {
			existing = null;
		}
		if(existing != null)
		{
			model.addAttribute("error", "Email already registered!");
			return "Register";
		}
		this.userServices.addUser(user);
		model.addAttribute("adminLogin", new AdminLogin());
		model.addAttribute("success", "Registration successful! Please login.");
		return "Login";
	}
}
