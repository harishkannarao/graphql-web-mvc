package com.harishkannarao.springboot.graphqlwebmvc.model;

public record CreateBookRes(
	Boolean success,
	String message,
	Book book
) implements MutationResponse {
}
