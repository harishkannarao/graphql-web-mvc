package com.harishkannarao.springboot.graphqlwebmvc.model;

import java.time.Instant;

public record DbEntity<T>(
	T data,
	Instant createdTime,
	Instant updatedTime
) {
}
