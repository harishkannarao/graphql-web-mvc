package com.harishkannarao.springboot.graphqlwebmvc.model;

import java.util.UUID;

public record PublisherInput(
	UUID id,
	String name
) {
}
