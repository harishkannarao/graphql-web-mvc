package com.harishkannarao.springboot.graphqlwebmvc.model;

public record GraphqlRequest(
	String query,
	GraphqlVariables variables
) {
}
