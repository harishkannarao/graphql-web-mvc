package com.harishkannarao.springboot.graphqlwebmvc.model;

import java.math.BigDecimal;
import java.util.Optional;

public record Book(
	String id,
	String name,
	BigDecimal rating,
	String isbn,
	Optional<java.time.OffsetDateTime> publishedDateTime) {
}
