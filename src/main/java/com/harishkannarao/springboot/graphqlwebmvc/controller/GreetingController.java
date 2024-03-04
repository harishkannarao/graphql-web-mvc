package com.harishkannarao.springboot.graphqlwebmvc.controller;

import com.harishkannarao.springboot.graphqlwebmvc.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GreetingController {

	private final Logger logger = LoggerFactory.getLogger(GreetingController.class);

	@QueryMapping(name = "greeting")
	public String handleGreeting(
		@Argument(name = "name")
		String inputName) {
		logger.info("Generating greeting for input {} with request id {}",
			inputName, MDC.get(Constants.X_REQUEST_ID));
		return "Hello, " + inputName + "!";
	}
}
