package com.harishkannarao.springboot.graphqlwebmvc.model.publisher;

import java.util.List;

public record GetPublishersGqlResponse(
	GetPublishersGqlData data,
	List<PublisherGqlError> errors
) {
}
