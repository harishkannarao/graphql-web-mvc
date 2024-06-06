package com.harishkannarao.springboot.graphqlwebmvc.client.rest.dto;

import com.harishkannarao.springboot.graphqlwebmvc.model.Retailer;

import java.util.List;

public record BookWithRetailers(
	String bookId,
	List<Retailer> retailers
) {
}
