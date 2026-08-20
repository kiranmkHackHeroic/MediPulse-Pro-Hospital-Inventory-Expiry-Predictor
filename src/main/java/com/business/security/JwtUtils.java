package com.business.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

	// 256-bit secret key for signing HS256 tokens
	private static final String SECRET_STRING = "NexusSoftEnterpriseCloudSaaSSecureSecretKey2026WithHighEntropy!";
	private static final Key SIGNING_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());

	// Token validity: 24 Hours
	private static final long JWT_EXPIRATION_MS = 24 * 60 * 60 * 1000L;

	public String generateToken(String username, String role, String displayName) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("role", role);
		claims.put("name", displayName);

		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_MS);

		return Jwts.builder()
				.setClaims(claims)
				.setSubject(username)
				.setIssuedAt(now)
				.setExpiration(expiryDate)
				.signWith(SIGNING_KEY, SignatureAlgorithm.HS256)
				.compact();
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public String extractRole(String token) {
		Claims claims = extractAllClaims(token);
		return (String) claims.get("role");
	}

	public String extractDisplayName(String token) {
		Claims claims = extractAllClaims(token);
		return (String) claims.get("name");
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(SIGNING_KEY)
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	public boolean isTokenExpired(String token) {
		try {
			return extractExpiration(token).before(new Date());
		} catch (Exception e) {
			return true;
		}
	}

	public boolean validateToken(String token, String username) {
		try {
			final String tokenUsername = extractUsername(token);
			return (tokenUsername.equals(username) && !isTokenExpired(token));
		} catch (Exception e) {
			return false;
		}
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(SIGNING_KEY).build().parseClaimsJws(token);
			return !isTokenExpired(token);
		} catch (Exception e) {
			return false;
		}
	}
}
