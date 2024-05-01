package com.harishkannarao.springboot.graphqlwebmvc.configuration;

import io.netty.handler.logging.LogLevel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

@Configuration
public class HttpGraphQlClientConfiguration {
	@Bean
	public HttpGraphQlClient createHttpGraphQlClient() {
		final HttpClient wiretappedHttpClient = HttpClient.create()
			.wiretap("reactor.netty.http.client.HttpClient",
				LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);
		WebClient client =
			WebClient.builder()
				.clientConnector(new ReactorClientHttpConnector(wiretappedHttpClient))
				.build();

		return HttpGraphQlClient.create(client);
	}
}
