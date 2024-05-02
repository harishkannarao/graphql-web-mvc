package com.harishkannarao.springboot.graphqlwebmvc.client.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.client.graphql.dto.BookWithPublishers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

@Component
public class PublisherGraphqlClient {

	private final HttpGraphQlClient httpGraphQlClient;

	public PublisherGraphqlClient(
		HttpGraphQlClient httpGraphQlClient,
		@Value("${third-party.publisher-service.graphql-url}") String url) {
		this.httpGraphQlClient = httpGraphQlClient.mutate().url(url).build();
	}

	public List<BookWithPublishers> queryPublishers(Set<String> bookIds) {
		return httpGraphQlClient.documentName("publisher/getPublishersByBooks")
			.variable("bookIds", bookIds)
			.retrieve("getPublishersByBooks")
			.toEntityList(BookWithPublishers.class)
			.block();
	}
}
