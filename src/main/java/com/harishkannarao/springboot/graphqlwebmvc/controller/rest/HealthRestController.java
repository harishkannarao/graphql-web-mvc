package com.harishkannarao.springboot.graphqlwebmvc.controller.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthRestController {

	private final Map<String, String> result = Map.of("status", "UP");
	private final ResponseEntity<Map<String, String>> response = ResponseEntity.ok(result);

	@GetMapping(path = "/health")
	public ResponseEntity<Map<String, String>> handleHealthRequest() {
		return response;
	}
}
