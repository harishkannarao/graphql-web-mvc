package com.harishkannarao.springboot.graphqlwebmvc.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static com.harishkannarao.springboot.graphqlwebmvc.util.Constants.X_REQUEST_ID;

public class RequestTracingFilter extends OncePerRequestFilter {
	public static final String NAME = "requestIdFilterBean";
	public static final String PATH = "/*";


	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
		String requestId = Optional.ofNullable(httpServletRequest.getHeader(X_REQUEST_ID))
			.orElseGet(() -> UUID.randomUUID().toString());
		httpServletRequest.setAttribute(X_REQUEST_ID, requestId);
		MDC.put(X_REQUEST_ID, requestId);
		httpServletResponse.addHeader(X_REQUEST_ID, requestId);
		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}
}
