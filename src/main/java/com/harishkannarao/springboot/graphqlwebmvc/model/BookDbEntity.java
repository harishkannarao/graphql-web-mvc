package com.harishkannarao.springboot.graphqlwebmvc.model;

import java.time.Instant;

public record BookDbEntity(
	Book book,
	Instant createdTime,
	Instant updatedTime
) {
}
