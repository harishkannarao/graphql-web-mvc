package com.harishkannarao.springboot.graphqlwebmvc.client.graphql.dto;

import org.springframework.graphql.ResponseError;

import java.util.List;

public record CreatePublisherMutationResult(
	List<ResponseError> errors,
	boolean data
) {
}
