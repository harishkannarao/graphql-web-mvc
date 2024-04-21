package com.harishkannarao.springboot.graphqlwebmvc.model;

public record CreateAuthorResponse(
	Boolean success,
	String message,
	Author author
) implements MutationResponse {
}
