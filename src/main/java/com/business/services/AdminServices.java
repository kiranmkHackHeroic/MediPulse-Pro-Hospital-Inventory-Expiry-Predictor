package com.business.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.business.entities.Admin;
import com.business.repositories.AdminRepository;

@Service
public class AdminServices
{
	@Autowired
	private AdminRepository adminRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	//Get All Admins
	public List<Admin> getAll()
	{
		List<Admin> admins = (List<Admin>) this.adminRepository.findAll();
		return admins;
	}

	//Get Single Admin
	public Admin getAdmin(int id)
	{
		Optional<Admin> optional = this.adminRepository.findById(id);
		return optional.orElse(null);
	}

	//Update Admin
	public void update(@NonNull Admin admin, int id)
	{
		admin.setAdminId(id);
		if (admin.getAdminPassword() != null && !admin.getAdminPassword().startsWith("$2a$")) {
			admin.setAdminPassword(passwordEncoder.encode(admin.getAdminPassword()));
		}
		this.adminRepository.save(admin);
	}
	
	//delete Admin
	public void delete(int id)
	{
		this.adminRepository.deleteById(id);
	}
	
	//add Admin
	public void addAdmin(@NonNull Admin admin)
	{
		if (admin.getAdminPassword() != null && !admin.getAdminPassword().startsWith("$2a$")) {
			admin.setAdminPassword(passwordEncoder.encode(admin.getAdminPassword()));
		}
		this.adminRepository.save(admin);
	}
	
	//Validating Admin login
	public boolean validateAdminCredentials(String email, String password)
	{
		Admin admin = adminRepository.findByAdminEmail(email);
		if (admin != null && admin.getAdminPassword() != null)
		{
			// Check BCrypt hash
			if (admin.getAdminPassword().startsWith("$2a$")) {
				return passwordEncoder.matches(password, admin.getAdminPassword());
			} else {
				// Legacy plain text fallback with auto-migration to BCrypt
				if (admin.getAdminPassword().equals(password)) {
					admin.setAdminPassword(passwordEncoder.encode(password));
					this.adminRepository.save(admin);
					return true;
				}
			}
		}
		return false;
	}
}
