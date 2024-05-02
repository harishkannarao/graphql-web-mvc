package com.harishkannarao.springboot.graphqlwebmvc.client.graphql.dto;

import com.harishkannarao.springboot.graphqlwebmvc.model.Publisher;

import java.util.List;

public record BookWithPublishers(
	String bookId,
	List<Publisher> publishers
) {
}
