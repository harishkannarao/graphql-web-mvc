package com.harishkannarao.springboot.graphqlwebmvc.model;

public record CreatePublishersResponse(
	Boolean success,
	String message
) implements MutationResponse {
}
