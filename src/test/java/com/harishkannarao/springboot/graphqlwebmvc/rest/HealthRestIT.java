package com.harishkannarao.springboot.graphqlwebmvc.rest;

import com.harishkannarao.springboot.graphqlwebmvc.AbstractBaseIT;
import com.harishkannarao.springboot.graphqlwebmvc.model.GreetingResponse;
import com.harishkannarao.springboot.graphqlwebmvc.util.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class HealthRestIT extends AbstractBaseIT {

	private final TestRestTemplate testRestTemplate;

	@Autowired
	public HealthRestIT(TestRestTemplate testRestTemplate) {
		this.testRestTemplate = testRestTemplate;
	}

	@Test
	public void get_health_status_using_rest_endpoint() {
		final ResponseEntity<String> result = testRestTemplate
			.getForEntity("/health", String.class);

		assertThat(result.getStatusCode().value()).isEqualTo(200);
		String entity = Objects.requireNonNull(result.getBody());
		assertThat(entity)
			.contains("status")
			.contains("UP");
	}
}
