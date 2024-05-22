package com.harishkannarao.springboot.graphqlwebmvc.model.publisher;

import java.util.List;

public record CreatePublishersGqlResponse(
	CreatePublishersGqlData data,
	List<PublisherGqlError> errors
) {
}
