package com.harishkannarao.springboot.graphqlwebmvc.unit.controller.rest;

import com.harishkannarao.springboot.graphqlwebmvc.controller.rest.GreetingRestController;
import com.harishkannarao.springboot.graphqlwebmvc.model.GreetingResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class GreetingRestControllerTest {

	private final GreetingRestController underTest = new GreetingRestController();

	@Test
	public void handleGreeting_returns_greeting_with_given_name() {
		String inputName = "test";
		ResponseEntity<GreetingResponseDto> result = underTest.handleGreeting(inputName);
		assertThat(result.getStatusCode().value()).isEqualTo(200);

		GreetingResponseDto entity = Objects.requireNonNull(result.getBody());
		assertThat(entity.message()).isEqualTo("Hello %s!".formatted(inputName));
	}
}
