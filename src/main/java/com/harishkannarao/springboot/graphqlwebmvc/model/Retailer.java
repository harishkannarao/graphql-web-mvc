package com.harishkannarao.springboot.graphqlwebmvc.model;

import java.util.UUID;

public record Retailer(
	UUID id,
	String name
) {
}
