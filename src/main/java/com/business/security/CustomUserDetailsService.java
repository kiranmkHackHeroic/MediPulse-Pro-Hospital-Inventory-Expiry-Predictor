package com.business.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.business.entities.Admin;
import com.business.entities.User;
import com.business.repositories.AdminRepository;
import com.business.repositories.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AdminRepository adminRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// First check if user exists
		User user = userRepository.findUserByUemail(email);
		if (user != null) {
			return new CustomUserDetails(
					user.getUemail(),
					user.getUpassword(),
					user.getUname(),
					"ROLE_USER"
			);
		}

		// Check if admin exists
		Admin admin = adminRepository.findByAdminEmail(email);
		if (admin != null) {
			return new CustomUserDetails(
					admin.getAdminEmail(),
					admin.getAdminPassword(),
					admin.getAdminName(),
					"ROLE_ADMIN"
			);
		}

		throw new UsernameNotFoundException("User or Admin not found with email: " + email);
	}
}
