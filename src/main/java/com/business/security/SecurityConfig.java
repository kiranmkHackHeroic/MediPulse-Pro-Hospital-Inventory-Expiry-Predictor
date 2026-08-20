package com.business.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private JwtAuthenticationFilter jwtAuthFilter;

	@Autowired
	private UserDetailsService userDetailsService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				// Static resources
				.requestMatchers("/css/**", "/Images/**", "/Videos/**", "/JavaScript/**", "/favicon.ico").permitAll()
				// Public web pages & Auth endpoints
				.requestMatchers("/", "/home", "/products", "/about", "/location", "/login", "/register", "/registerUser", "/userlogin", "/adminLogin", "/logout", "/api/ai/**").permitAll()
				// Admin endpoints require ROLE_ADMIN
				.requestMatchers("/admin/**", "/addAdmin", "/addingAdmin", "/updateAdmin/**", "/deleteAdmin/**",
				                 "/addProduct", "/addUser", "/product/adding", "/product/updatingProduct/**",
				                 "/deleteProduct/**", "/updatingUser/**", "/deleteUser/**").hasAuthority("ROLE_ADMIN")
				// All other requests require authentication
				.anyRequest().authenticated()
			)
			.exceptionHandling(exceptions -> exceptions
				.authenticationEntryPoint((request, response, authException) -> {
					// Redirect unauthenticated browser requests to login page
					String acceptHeader = request.getHeader("Accept");
					if (acceptHeader != null && acceptHeader.contains("text/html")) {
						response.sendRedirect("/login?unauthorized=true");
					} else {
						response.sendError(401, "Unauthorized");
					}
				})
				.accessDeniedHandler((request, response, accessDeniedException) -> {
					String acceptHeader = request.getHeader("Accept");
					if (acceptHeader != null && acceptHeader.contains("text/html")) {
						response.sendRedirect("/login?forbidden=true");
					} else {
						response.sendError(403, "Forbidden");
					}
				})
			)
			.authenticationProvider(authenticationProvider())
			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
