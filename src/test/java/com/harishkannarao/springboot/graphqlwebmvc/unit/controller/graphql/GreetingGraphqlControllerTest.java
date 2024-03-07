package com.harishkannarao.springboot.graphqlwebmvc.unit.controller.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.controller.graphql.GreetingGraphqlController;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GreetingGraphqlControllerTest {

	private final GreetingGraphqlController underTest = new GreetingGraphqlController();

	@Test
	public void handleGreeting_returns_greeting_with_given_name() {
		String inputName = "test";
		String requestId = UUID.randomUUID().toString();
		String result = underTest.handleGreeting(inputName, requestId);
		assertThat(result).contains("Hello, %s!".formatted(inputName));
	}

	@Test
	public void handleGreeting_throws_runtime_exception() {
		String inputName = "throw-error";
		String requestId = UUID.randomUUID().toString();

		RuntimeException result = assertThrows(RuntimeException.class,
			() -> underTest.handleGreeting(inputName, requestId));
		assertThat(result.getMessage()).isEqualTo("Artificial Error !!!");
	}
}
