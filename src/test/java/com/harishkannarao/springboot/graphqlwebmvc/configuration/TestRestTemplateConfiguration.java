package com.harishkannarao.springboot.graphqlwebmvc.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestRestTemplateConfiguration {

    @Bean
    public TestRestTemplate createTestRestTemplate(
            @Value("${test.application.baseUrl}") String rootUrl
    ) {
        return new TestRestTemplate(new RestTemplateBuilder().rootUri(rootUrl));
    }
}
