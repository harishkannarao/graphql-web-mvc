package com.harishkannarao.springboot.graphqlwebmvc.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;

public record BookInput(
	String id,
	String name,
	BigDecimal rating,
	String isbn,
	Optional<OffsetDateTime> publishedDateTime) {
}
