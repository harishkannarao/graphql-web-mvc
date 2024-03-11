package com.harishkannarao.springboot.graphqlwebmvc.dao.entity;

import java.time.Instant;

public record DbEntity<T>(
	T data,
	Instant createdTime,
	Instant updatedTime
) {
}
