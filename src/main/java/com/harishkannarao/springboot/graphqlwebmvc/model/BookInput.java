package com.harishkannarao.springboot.graphqlwebmvc.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record BookInput(
	String id,
	String name,
	BigDecimal rating,
	String isbn,
	OffsetDateTime publishedDateTime) {
}
