package com.harishkannarao.springboot.graphqlwebmvc.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.web.reactive.server.WebTestClient;

@Configuration
public class HttpGraphQlTesterConfiguration {
    @Bean
    public HttpGraphQlTester createHttpGrqphQlTester(
            @Value("${test.application.graphqlUrl}") String graphqlUrl
    ) {
        WebTestClient client =
                WebTestClient.bindToServer()
                        .baseUrl(graphqlUrl)
                        .build();

        return HttpGraphQlTester.create(client);
    }
}
