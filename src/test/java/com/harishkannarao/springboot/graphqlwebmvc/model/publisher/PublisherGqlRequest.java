package com.harishkannarao.springboot.graphqlwebmvc.model.publisher;

public record PublisherGqlRequest(
	String query,
	PublisherGqlVariables variables
) {
}
