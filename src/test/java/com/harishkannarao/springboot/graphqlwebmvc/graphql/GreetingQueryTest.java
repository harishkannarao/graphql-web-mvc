package com.harishkannarao.springboot.graphqlwebmvc.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.AbstractBaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import static org.assertj.core.api.Assertions.assertThat;

public class GreetingQueryTest extends AbstractBaseIntegrationTest {

    private final String graphqlUrl;

    @Autowired
    public GreetingQueryTest(
            @Value("${test.application.graphqlUrl}") String graphqlUrl
    ) {
        this.graphqlUrl = graphqlUrl;
    }

    @Test
    public void test_greeting_query_with_parameters() {
        assertThat(graphqlUrl).isNotBlank();
    }
}
