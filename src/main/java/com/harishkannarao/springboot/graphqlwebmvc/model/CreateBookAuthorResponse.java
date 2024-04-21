package com.harishkannarao.springboot.graphqlwebmvc.model;

public record CreateBookAuthorResponse(
	Boolean success,
	String message,
	Author author,
	Book book
) implements MutationResponse {
}
