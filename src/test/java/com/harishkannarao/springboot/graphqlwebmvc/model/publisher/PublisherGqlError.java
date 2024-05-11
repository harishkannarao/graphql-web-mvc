package com.harishkannarao.springboot.graphqlwebmvc.model.publisher;

import java.util.List;

public record PublisherGqlError(
	String message,
	List<String> path
) {
}
