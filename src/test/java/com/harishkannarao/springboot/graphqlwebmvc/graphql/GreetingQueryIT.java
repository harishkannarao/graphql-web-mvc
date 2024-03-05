package com.harishkannarao.springboot.graphqlwebmvc.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.AbstractBaseIT;
import com.harishkannarao.springboot.graphqlwebmvc.util.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class GreetingQueryIT extends AbstractBaseIT {

	private final HttpGraphQlTester httpGraphQlTester;

	@Autowired
	public GreetingQueryIT(
		HttpGraphQlTester httpGraphQlTester
	) {
		this.httpGraphQlTester = httpGraphQlTester;
	}

	@Test
	public void test_greeting_query_with_parameters() {
		String inputName = "hello";
		String result = httpGraphQlTester
			.documentName("query/queryGreetingWithParam")
			.variable("name", inputName)
			.execute()
			.path("greeting")
			.entity(String.class)
			.get();

		assertThat(result).contains("Hello, %s!".formatted(inputName));
	}

	@Test
	public void test_greeting_query_without_parameters_returns_data_with_default_value() {
		String result = httpGraphQlTester.documentName("query/queryGreetingWithoutParam")
			.execute()
			.path("greeting")
			.entity(String.class)
			.get();

		assertThat(result).contains("Hello, Spring!");
	}

	@Test
	public void test_greeting_with_alias() {
		String inputName = "hello";
		String requestId = UUID.randomUUID().toString();
		GraphQlTester.Response result = httpGraphQlTester
			.mutate()
			.header(Constants.X_REQUEST_ID, requestId)
			.build()
			.documentName("query/queryGreetingWithAlias")
			.variable("name", inputName)
			.execute();
		String greetingWithName = result
			.path("greetingWithName")
			.entity(String.class)
			.get();

		assertThat(greetingWithName).contains("Hello, %s!".formatted(inputName));
		assertThat(greetingWithName).contains(requestId);

		String greetingWithoutName = result
			.path("greetingWithoutName")
			.entity(String.class)
			.get();

		assertThat(greetingWithoutName).contains("Hello, Spring!");
		assertThat(greetingWithoutName).contains(requestId);
	}

	@Test
	public void test_greeting_returns_error_on_blank_name() {
		String inputName = "hello";
		GraphQlTester.Response result = httpGraphQlTester.documentName("query/queryGreetingWithConstraintError")
			.variable("name", inputName)
			.execute();

		result.errors()
			.satisfy(errors -> assertThat(errors)
				.anySatisfy(error -> {
					assertThat(error.getMessage()).isEqualTo("/greetingWithBlankName/name must not be blank");
					assertThat(error.getPath()).isEqualTo("greetingWithBlankName");
				}));
	}

	@Test
	public void test_greeting_returns_error_if_name_more_than_6_characters() {
		String inputName = "hello there";
		GraphQlTester.Response result = httpGraphQlTester
			.documentName("query/queryGreetingWithParam")
			.variable("name", inputName)
			.execute();

		result.errors()
			.satisfy(errors -> assertThat(errors)
				.anySatisfy(error -> {
					assertThat(error.getMessage()).isEqualTo("/greeting/name must match \".{0,6}\"");
					assertThat(error.getPath()).isEqualTo("greeting");
				}));
	}
}
