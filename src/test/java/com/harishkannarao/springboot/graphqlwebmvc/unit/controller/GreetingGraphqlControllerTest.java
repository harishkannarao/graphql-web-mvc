package com.harishkannarao.springboot.graphqlwebmvc.unit.controller;

import com.harishkannarao.springboot.graphqlwebmvc.controller.GreetingGraphqlController;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GreetingGraphqlControllerTest {

	private final GreetingGraphqlController underTest = new GreetingGraphqlController();

	@Test
	public void handleGreeting_returns_greeting_with_given_name() {
		String inputName = "test";
		String requestId = UUID.randomUUID().toString();
		String result = underTest.handleGreeting(inputName, requestId);
		assertThat(result).contains("Hello, %s!".formatted(inputName));
	}
}
