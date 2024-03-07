package com.harishkannarao.springboot.graphqlwebmvc.configuration;

import com.harishkannarao.springboot.graphqlwebmvc.client.rest.RestClientAccessLoggingInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.time.Duration;
import java.util.List;

@Configuration
public class TestRestTemplateConfiguration {

	@Bean
	public TestRestTemplate createTestRestTemplate(
		@Value("${test.application.baseUrl}") String rootUrl
	) {
		SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
		BufferingClientHttpRequestFactory clientHttpRequestFactory =
			new BufferingClientHttpRequestFactory(simpleClientHttpRequestFactory);
		return new TestRestTemplate(new RestTemplateBuilder()
			.rootUri(rootUrl)
			.requestFactory(() -> clientHttpRequestFactory)
			.additionalInterceptors(List.of(new RestClientAccessLoggingInterceptor()))
		);
	}
}
