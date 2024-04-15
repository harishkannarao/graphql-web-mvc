package com.harishkannarao.springboot.graphqlwebmvc.model;

import java.math.BigDecimal;

public record Book(
	String id,
	String name,
	BigDecimal rating,
	String isbn) {
}
