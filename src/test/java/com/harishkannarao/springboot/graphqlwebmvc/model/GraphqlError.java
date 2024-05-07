package com.harishkannarao.springboot.graphqlwebmvc.model;

import java.util.List;

public record GraphqlError(
	String message,
	List<String> path
) {
}
