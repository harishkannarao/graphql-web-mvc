package com.harishkannarao.springboot.graphqlwebmvc.model.publisher;

import java.util.List;

public record PublisherGqlVariables(
	List<String> bookIds
) {
}
