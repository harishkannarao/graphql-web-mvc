package com.harishkannarao.springboot.graphqlwebmvc.model.publisher;

public record CreatePublishersGqlRequest(
	String query,
	CreatePublishersGqlVariables variables
) {
}
