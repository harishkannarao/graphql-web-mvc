package com.harishkannarao.springboot.graphqlwebmvc.model;

import java.util.List;

public record GraphqlResponse(
	GraphqlData data,
	List<GraphqlError> errors
) {
}
