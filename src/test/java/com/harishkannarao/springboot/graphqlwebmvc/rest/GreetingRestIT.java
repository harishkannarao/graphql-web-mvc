package com.harishkannarao.springboot.graphqlwebmvc.rest;

import com.harishkannarao.springboot.graphqlwebmvc.AbstractBaseIT;
import com.harishkannarao.springboot.graphqlwebmvc.model.GreetingResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class GreetingRestIT extends AbstractBaseIT {

	private final TestRestTemplate testRestTemplate;

	@Autowired
	public GreetingRestIT(TestRestTemplate testRestTemplate) {
		this.testRestTemplate = testRestTemplate;
	}

	@Test
	public void get_greeting_using_rest_endpoint() {
		final String inputName = "hello";

		final ResponseEntity<GreetingResponseDto> result = testRestTemplate
			.getForEntity("/rest/greeting?name=" + inputName, GreetingResponseDto.class);

		assertThat(result.getStatusCode().value()).isEqualTo(200);
		GreetingResponseDto entity = Objects.requireNonNull(result.getBody());
		assertThat(entity.message()).isEqualTo("Hello %s!".formatted(inputName));
	}
}