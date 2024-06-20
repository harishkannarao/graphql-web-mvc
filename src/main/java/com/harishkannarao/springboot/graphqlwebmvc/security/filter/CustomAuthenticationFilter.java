package com.harishkannarao.springboot.graphqlwebmvc.security.filter;

import com.harishkannarao.springboot.graphqlwebmvc.security.resolver.CustomAuthenticationResolver;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class CustomAuthenticationFilter extends OncePerRequestFilter {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final CustomAuthenticationResolver customAuthenticationResolver;

	@Autowired
	public CustomAuthenticationFilter(CustomAuthenticationResolver customAuthenticationResolver) {
		this.customAuthenticationResolver = customAuthenticationResolver;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		try {
			Optional<Authentication> resolvedAuthentication = customAuthenticationResolver.resolve(request);
			resolvedAuthentication.ifPresent(authentication -> SecurityContextHolder.getContext().setAuthentication(authentication));
		} catch (Exception e) {
			logger.error("Error resolving authentication: " + e.getMessage(), e);
		} finally {
			filterChain.doFilter(request, response);
		}
	}
}
