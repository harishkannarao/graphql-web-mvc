package com.harishkannarao.springboot.graphqlwebmvc.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.AbstractBaseIntegrationTest;
import com.harishkannarao.springboot.graphqlwebmvc.util.FileReaderUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.HttpGraphQlTester;

import static org.assertj.core.api.Assertions.assertThat;

public class GreetingQueryTest extends AbstractBaseIntegrationTest {

    private final HttpGraphQlTester httpGraphQlTester;

    @Autowired
    public GreetingQueryTest(
            HttpGraphQlTester httpGraphQlTester
    ) {
        this.httpGraphQlTester = httpGraphQlTester;
    }

    @Test
    public void test_greeting_query_with_parameters() {
        String inputName = "hello";
        String result = httpGraphQlTester.documentName("queryGreeting")
                .variable("name", inputName)
                .execute()
                .path("greeting")
                .entity(String.class)
                .get();

        assertThat(result).isEqualTo("Hello, %s!".formatted(inputName));

        String document = FileReaderUtil.readFile("graphql-test/queryGreeting.graphql");
        assertThat(document).isEqualTo("query Greeting($name: String!) {\n    greeting(name: $name)\n}");
    }
}
