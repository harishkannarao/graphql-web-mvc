package com.harishkannarao.springboot.graphqlwebmvc.controller.rest;

import com.harishkannarao.springboot.graphqlwebmvc.exception.ArtificialException;
import com.harishkannarao.springboot.graphqlwebmvc.model.GreetingRes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingRestController {

	@GetMapping(path = "/rest/greeting")
	public ResponseEntity<GreetingRes> handleGreeting(
		@RequestParam(name = "name", required = false, defaultValue = "Spring")
		final String inputName
	) {
		if (inputName.equals("throw-error")) {
			throw new ArtificialException("Artificial Error !!!");
		}
		return ResponseEntity.ok(new GreetingRes("Hello %s!".formatted(inputName)));
	}
}
