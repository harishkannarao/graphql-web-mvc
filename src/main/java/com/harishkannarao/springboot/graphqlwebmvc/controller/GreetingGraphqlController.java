package com.harishkannarao.springboot.graphqlwebmvc.controller;

import com.harishkannarao.springboot.graphqlwebmvc.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GreetingGraphqlController {

	private final Logger logger = LoggerFactory.getLogger(GreetingGraphqlController.class);

	@QueryMapping(name = "greeting")
	public String handleGreeting(
		@Argument(name = "name") final String inputName,
		@ContextValue(name = Constants.X_REQUEST_ID) final String requestId) {
		logger.info("Generating greeting for input {} with request id {}",
			inputName, requestId);
		return "Hello, %s! with requestId %s".formatted(inputName, requestId);
	}
}
