package com.harishkannarao.springboot.graphqlwebmvc.client.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.client.graphql.dto.BookWithPublishers;
import com.harishkannarao.springboot.graphqlwebmvc.client.graphql.dto.PublisherQueryResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.client.ClientResponseField;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Component
public class PublisherGraphqlClient {

	private final HttpGraphQlClient httpGraphQlClient;

	public PublisherGraphqlClient(
		HttpGraphQlClient httpGraphQlClient,
		@Value("${third-party.publisher-service.graphql-url}") String url) {
		this.httpGraphQlClient = httpGraphQlClient.mutate().url(url).build();
	}

	public CompletableFuture<PublisherQueryResult> queryPublishers(Set<String> bookIds) {
		return httpGraphQlClient.documentName("publisher/getPublishersByBooks")
			.variable("bookIds", bookIds)
			.execute()
			.map(response -> {
				if (!response.isValid()) { // response does not have data, only errors
					return new PublisherQueryResult(response.getErrors(), Collections.emptyList());
				}
				ClientResponseField field = response.field("getPublishersByBooks");
				if (!field.getErrors().isEmpty()) { // response has field errors
					return new PublisherQueryResult(field.getErrors(), Collections.emptyList());
				}
				else {
					return new PublisherQueryResult(Collections.emptyList(), field.toEntityList(BookWithPublishers.class));
				}
			})
			.toFuture();
	}
}
