package com.harishkannarao.springboot.graphqlwebmvc.rest;

import com.harishkannarao.springboot.graphqlwebmvc.AbstractBaseIT;
import com.harishkannarao.springboot.graphqlwebmvc.model.GreetingRes;
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

public class GreetingRestIT extends AbstractBaseIT {

	private final TestRestTemplate testRestTemplate;

	@Autowired
	public GreetingRestIT(TestRestTemplate testRestTemplate) {
		this.testRestTemplate = testRestTemplate;
	}

	@Test
	public void get_greeting_using_rest_endpoint() {
		final String inputName = "hello";

		final ResponseEntity<GreetingRes> result = testRestTemplate
			.getForEntity("/rest/greeting?name=" + inputName, GreetingRes.class);

		assertThat(result.getStatusCode().value()).isEqualTo(200);
		GreetingRes entity = Objects.requireNonNull(result.getBody());
		assertThat(entity.message()).isEqualTo("Hello %s!".formatted(inputName));
	}

	@Test
	public void get_greeting_returns_400_bad_request() {
		final String inputName = "throw-error";
		final String requestId = UUID.randomUUID().toString();

		final HttpHeaders reqHeaders = new HttpHeaders();
		reqHeaders.add(Constants.X_REQUEST_ID, requestId);
		final HttpEntity<Void> reqEntity = new HttpEntity<>(reqHeaders);
		final ResponseEntity<String> result = testRestTemplate
			.exchange("/rest/greeting?name=" + inputName, HttpMethod.GET, reqEntity, String.class);

		assertThat(result.getStatusCode().value()).isEqualTo(400);
		assertThat(result.getBody())
			.contains("Artificial Error !!!")
			.contains(requestId)
			.contains("Bad Request")
			.contains("400")
			.contains("/graphql-web-mvc/rest/greeting");
	}
}
