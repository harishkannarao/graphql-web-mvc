package com.harishkannarao.springboot.graphqlwebmvc.model.publisher;

import java.util.List;

public record PublisherGqlResponse(
	PublisherGqlData data,
	List<PublisherGqlError> errors
) {
}
