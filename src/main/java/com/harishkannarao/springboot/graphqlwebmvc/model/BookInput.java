package com.harishkannarao.springboot.graphqlwebmvc.model;

import java.math.BigDecimal;

public record BookInput(
	String id,
	String name,
	BigDecimal rating,
	String isbn) {
}
