package com.harishkannarao.springboot.graphqlwebmvc.client.graphql.dto;

import org.springframework.graphql.ResponseError;

import java.util.List;

public record PublisherQueryResult(
	List<ResponseError> errors,
	List<BookWithPublishers> data
) {
}
