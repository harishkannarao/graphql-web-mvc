package com.harishkannarao.springboot.graphqlwebmvc.model;

public record CreateBookResponse(
	Boolean success,
	String message,
	Book book
) implements MutationResponse {
}
