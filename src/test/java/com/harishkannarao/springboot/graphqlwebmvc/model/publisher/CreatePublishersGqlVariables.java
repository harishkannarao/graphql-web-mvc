package com.harishkannarao.springboot.graphqlwebmvc.model.publisher;

import com.harishkannarao.springboot.graphqlwebmvc.model.PublisherInput;

import java.util.Set;

public record CreatePublishersGqlVariables(
	Set<PublisherInput> publishers
) {
}
