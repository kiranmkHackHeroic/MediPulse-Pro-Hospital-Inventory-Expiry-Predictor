package com.business.controllers;

import java.security.Principal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.business.basiclogics.Logic;
import com.business.entities.Admin;
import com.business.entities.Orders;
import com.business.entities.Product;
import com.business.entities.User;
import com.business.loginCredentials.AdminLogin;
import com.business.loginCredentials.UserLogin;
import com.business.repositories.AdminRepository;
import com.business.security.JwtUtils;
import com.business.services.AdminServices;
import com.business.services.OrderServices;
import com.business.services.ProductServices;
import com.business.services.UserServices;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AdminController {

	@Autowired
	private UserServices services;

	@Autowired
	private AdminServices adminServices;

	@Autowired
	private AdminRepository adminRepository;

	@Autowired
	private ProductServices productServices;

	@Autowired
	private OrderServices orderServices;

	@Autowired
	private JwtUtils jwtUtils;

	// Helper to safely get current authenticated User from Spring Security Principal
	private User getCurrentUser(Principal principal) {
		if (principal != null) {
			return this.services.getUserByEmail(principal.getName());
		}
		return null;
	}

	// Validating Admin login with JWT token generation
	@GetMapping("/adminLogin")
	public String adminLogin(@ModelAttribute("adminLogin") AdminLogin login, HttpServletResponse response, Model model) {
		String email = login.getEmail();
		String password = login.getPassword();

		if (adminServices.validateAdminCredentials(email, password)) {
			Admin admin = this.adminRepository.findByAdminEmail(email);
			String displayName = admin != null ? admin.getAdminName() : "Admin";

			// Generate JWT Token
			String token = jwtUtils.generateToken(email, "ROLE_ADMIN", displayName);

			// Attach HttpOnly cookie
			Cookie jwtCookie = new Cookie("jwt_token", token);
			jwtCookie.setHttpOnly(true);
			jwtCookie.setPath("/");
			jwtCookie.setMaxAge(24 * 60 * 60);
			response.addCookie(jwtCookie);

			return "redirect:/admin/services";
		} else {
			model.addAttribute("error", "Invalid admin email or security key");
			return "Login";
		}
	}

	// Validating User / Client login with JWT token generation
	@GetMapping("/userlogin")
	public String userLogin(@ModelAttribute("userLogin") UserLogin login, HttpServletResponse response, Model model) {
		String email = login.getUserEmail();
		String password = login.getUserPassword();

		if (services.validateLoginCredentials(email, password)) {
			User user = this.services.getUserByEmail(email);
			String displayName = user != null ? user.getUname() : "Client";

			// Generate JWT Token
			String token = jwtUtils.generateToken(email, "ROLE_USER", displayName);

			// Attach HttpOnly cookie
			Cookie jwtCookie = new Cookie("jwt_token", token);
			jwtCookie.setHttpOnly(true);
			jwtCookie.setPath("/");
			jwtCookie.setMaxAge(24 * 60 * 60);
			response.addCookie(jwtCookie);

			return "redirect:/product/back";
		} else {
			model.addAttribute("error2", "Invalid client email or password");
			return "Login";
		}
	}

	// Logout endpoint to clear JWT cookie
	@GetMapping("/logout")
	public String logout(HttpServletResponse response) {
		// Clear JWT cookie
		Cookie cookie = new Cookie("jwt_token", null);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setMaxAge(0);
		response.addCookie(cookie);

		// Clear security context
		SecurityContextHolder.clearContext();

		return "redirect:/login?logout=true";
	}

	// Searching Product By Name in Client Portal
	@PostMapping("/product/search")
	public String searchHandler(@RequestParam("productName") String name, Principal principal, Model model) {
		User user = getCurrentUser(principal);
		Product product = this.productServices.getProductByName(name);

		if (user != null) {
			List<Orders> orders = this.orderServices.getOrdersForUser(user);
			model.addAttribute("orders", orders);
			model.addAttribute("name", user.getUname());
		}

		if (product == null) {
			model.addAttribute("message", "SORRY...! Software Module Unavailable");
			model.addAttribute("product", null);
			return "BuyProduct";
		}

		model.addAttribute("product", product);
		return "BuyProduct";
	}

	// Providing services in Admin Dashboard
	@GetMapping("/admin/services")
	public String returnBack(Model model) {
		List<User> users = this.services.getAllUser();
		List<Admin> admins = this.adminServices.getAll();
		List<Product> products = this.productServices.getAllProducts();
		List<Orders> orders = this.orderServices.getOrders();

		model.addAttribute("users", users);
		model.addAttribute("admins", admins);
		model.addAttribute("products", products);
		model.addAttribute("orders", orders);

		return "Admin_Page";
	}

	// Invoking addAdmin Page
	@GetMapping("/addAdmin")
	public String addAdminPage() {
		return "Add_Admin";
	}

	// Handling AddAdmin
	@PostMapping("/addingAdmin")
	public String addAdmin(@ModelAttribute Admin admin) {
		this.adminServices.addAdmin(admin);
		return "redirect:/admin/services";
	}

	// Invoking updateAdmin Page
	@GetMapping("/updateAdmin/{adminId}")
	public String update(@PathVariable("adminId") int id, Model model) {
		Admin admin = this.adminServices.getAdmin(id);
		model.addAttribute("admin", admin);
		return "Update_Admin";
	}

	// Handling Update Page
	@GetMapping("/updatingAdmin/{id}")
	public String updateAdmin(@ModelAttribute Admin admin, @PathVariable("id") int id) {
		this.adminServices.update(admin, id);
		return "redirect:/admin/services";
	}

	// Handling delete admin operation
	@GetMapping("/deleteAdmin/{id}")
	public String deleteAdmin(@PathVariable("id") int id) {
		this.adminServices.delete(id);
		return "redirect:/admin/services";
	}

	// Invoking AddProduct Page
	@GetMapping("/addProduct")
	public String addProduct() {
		return "Add_Product";
	}

	// Invoking Update Product Page
	@GetMapping("/updateProduct/{productId}")
	public String updateProduct(@PathVariable("productId") int id, Model model) {
		Product product = this.productServices.getProduct(id);
		model.addAttribute("product", product);
		return "Update_Product";
	}

	// Invoking AddUser Page
	@GetMapping("/addUser")
	public String addUser() {
		return "Add_User";
	}

	// Invoking UpdateUser Page
	@GetMapping("/updateUser/{userId}")
	public String updateUserPage(@PathVariable("userId") int id, Model model) {
		User user = this.services.getUser(id);
		model.addAttribute("user", user);
		return "Update_User";
	}

	// Placing Order / Software License Subscription
	@PostMapping("/product/order")
	public String orderHandler(@ModelAttribute Orders order, Principal principal, Model model) {
		User user = getCurrentUser(principal);
		double totalAmount = Logic.countTotal(order.getoPrice(), order.getoQuantity());

		order.setTotalAmmout(totalAmount);
		order.setUser(user);
		order.setOrderDate(new Date());

		this.orderServices.saveOrder(order);
		model.addAttribute("amount", totalAmount);
		return "Order_success";
	}

	// Returning to Client Workspace
	@GetMapping("/product/back")
	public String back(Principal principal, Model model) {
		User user = getCurrentUser(principal);

		if (user != null) {
			List<Orders> orders = this.orderServices.getOrdersForUser(user);
			model.addAttribute("orders", orders);
			model.addAttribute("name", user.getUname());
		}

		return "BuyProduct";
	}
}
