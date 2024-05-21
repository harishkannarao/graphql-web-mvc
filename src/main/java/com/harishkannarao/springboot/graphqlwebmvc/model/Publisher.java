package com.harishkannarao.springboot.graphqlwebmvc.model;

import java.util.UUID;

public record Publisher(
	UUID id,
	String name
) {
}
