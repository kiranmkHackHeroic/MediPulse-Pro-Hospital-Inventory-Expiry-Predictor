package com.business.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.business.entities.User;
import com.business.repositories.UserRepository;

@Service
public class UserServices 
{
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
		
	//Get All Users
	public List<User> getAllUser()
	{
		List<User> users = (List<User>) this.userRepository.findAll();
		return users;
	}
	
	//Get Single User
	public User getUser(int id)
	{
		Optional<User> optional = this.userRepository.findById(id);
		return optional.orElse(null);
	}
	
	//Get Single User By Email
	public User getUserByEmail(String email)
	{
		User user = this.userRepository.findUserByUemail(email);
		return user;
	}
	
	//Update
	public void updateUser(User user, int id)
	{
		user.setU_id(id);
		// Check if password needs hashing
		if (user.getUpassword() != null && !user.getUpassword().startsWith("$2a$")) {
			user.setUpassword(passwordEncoder.encode(user.getUpassword()));
		}
		this.userRepository.save(user);
	}
	
	//delete single User
	public void deleteUser(int id)
	{
		this.userRepository.deleteById(id);
	}

	//Add User
	public void addUser(User user)
	{
		// Hash password with BCrypt
		if (user.getUpassword() != null && !user.getUpassword().startsWith("$2a$")) {
			user.setUpassword(passwordEncoder.encode(user.getUpassword()));
		}
		this.userRepository.save(user);
	}
	
	public boolean validateLoginCredentials(String email, String password)
	{
		User u = this.userRepository.findUserByUemail(email);
		if (u != null && u.getUpassword() != null)
		{
			// Check if BCrypt matches
			if (u.getUpassword().startsWith("$2a$")) {
				return passwordEncoder.matches(password, u.getUpassword());
			} else {
				// Legacy plain-text fallback with auto-migration to BCrypt
				if (u.getUpassword().equals(password)) {
					u.setUpassword(passwordEncoder.encode(password));
					this.userRepository.save(u);
					return true;
				}
			}
		}
		return false;
	}
}
