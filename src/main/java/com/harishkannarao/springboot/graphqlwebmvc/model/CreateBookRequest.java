package com.harishkannarao.springboot.graphqlwebmvc.model;

import java.math.BigDecimal;

public record CreateBookRequest(
	String id,
	String name,
	BigDecimal rating) {
}
