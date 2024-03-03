package com.harishkannarao.springboot.graphqlwebmvc.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.AbstractBaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;

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
        String result = httpGraphQlTester.documentName("query/queryGreetingWithParam")
                .variable("name", inputName)
                .execute()
                .path("greeting")
                .entity(String.class)
                .get();

        assertThat(result).isEqualTo("Hello, %s!".formatted(inputName));
    }

    @Test
    public void test_greeting_query_without_parameters_returns_data_with_default_value() {
        String result = httpGraphQlTester.documentName("query/queryGreetingWithoutParam")
                .execute()
                .path("greeting")
                .entity(String.class)
                .get();

        assertThat(result).isEqualTo("Hello, Spring!");
    }

    @Test
    public void test_greeting_with_alias() {
        String inputName = "hello";
        GraphQlTester.Response result = httpGraphQlTester.documentName("query/queryGreetingWithAlias")
                .variable("name", inputName)
                .execute();
        String greetingWithName = result
                .path("greetingWithName")
                .entity(String.class)
                .get();

        assertThat(greetingWithName).isEqualTo("Hello, %s!".formatted(inputName));

        String greetingWithoutName = result
                .path("greetingWithoutName")
                .entity(String.class)
                .get();

        assertThat(greetingWithoutName).isEqualTo("Hello, Spring!");
    }
}
