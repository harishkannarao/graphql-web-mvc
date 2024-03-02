package com.harishkannarao.springboot.graphqlwebmvc.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.AbstractBaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
        String result = httpGraphQlTester.documentName("query/queryGreeting")
                .variable("name", inputName)
                .execute()
                .path("greeting")
                .entity(String.class)
                .get();

        assertThat(result).isEqualTo("Hello, %s!".formatted(inputName));
    }
}
