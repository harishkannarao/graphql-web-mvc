package com.harishkannarao.springboot.graphqlwebmvc.client.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.client.graphql.dto.BookWithPublishers;
import com.harishkannarao.springboot.graphqlwebmvc.client.graphql.dto.PublisherQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.client.ClientResponseField;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.harishkannarao.springboot.graphqlwebmvc.util.Constants.X_REQUEST_ID;

@Component
public class PublisherGraphqlClient {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private final HttpGraphQlClient httpGraphQlClient;

	public PublisherGraphqlClient(
		HttpGraphQlClient httpGraphQlClient,
		@Value("${third-party.publisher-service.graphql-url}") String url) {
		this.httpGraphQlClient = httpGraphQlClient.mutate().url(url).build();
	}

	public CompletableFuture<PublisherQueryResult> queryPublishers(Set<String> bookIds, String requestId) {
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
				} else {
					return new PublisherQueryResult(Collections.emptyList(), field.toEntityList(BookWithPublishers.class));
				}
			})
			.toFuture()
			.whenComplete((publisherQueryResult, throwable) -> {
				try {
					MDC.put(X_REQUEST_ID, requestId);
					Optional.ofNullable(throwable)
						.ifPresent(ex -> log.error(ex.getMessage(), ex));
				} finally {
					MDC.remove(X_REQUEST_ID);
				}
			});
	}
}
