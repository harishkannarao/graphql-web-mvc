package com.harishkannarao.springboot.graphqlwebmvc.model.publisher;

public record GetPublishersGqlRequest(
	String query,
	GetPublishersGqlVariables variables
) {
}
