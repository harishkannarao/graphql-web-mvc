package com.harishkannarao.springboot.graphqlwebmvc.model;

public record CreateBookResponseDto(
	Boolean success,
	String message,
	BookResponseDto book
) implements MutationResponse {
}
