package com.business.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails {

	private final String username;
	private final String password;
	private final String displayName;
	private final String role;
	private final Collection<? extends GrantedAuthority> authorities;

	public CustomUserDetails(String username, String password, String displayName, String role) {
		this.username = username;
		this.password = password;
		this.displayName = displayName;
		this.role = role;
		this.authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getRole() {
		return role;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
