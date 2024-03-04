package com.harishkannarao.springboot.graphqlwebmvc.configuration;

import io.netty.handler.logging.LogLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

@Configuration
public class HttpGraphQlTesterConfiguration {
	@Bean
	public HttpGraphQlTester createHttpGrqphQlTester(
		@Value("${test.application.graphqlUrl}") String graphqlUrl
	) {
		final HttpClient wiretappedHttpClient = HttpClient.create()
			.wiretap("reactor.netty.http.client.HttpClient",
				LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);
		WebTestClient client =
			WebTestClient.bindToServer()
				.baseUrl(graphqlUrl)
				.clientConnector(new ReactorClientHttpConnector(wiretappedHttpClient))
				.build();

		return HttpGraphQlTester.create(client);
	}
}
