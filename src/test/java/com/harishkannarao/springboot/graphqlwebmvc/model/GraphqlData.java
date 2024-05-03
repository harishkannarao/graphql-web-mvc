package com.harishkannarao.springboot.graphqlwebmvc.model;

import com.harishkannarao.springboot.graphqlwebmvc.client.graphql.dto.BookWithPublishers;

import java.util.List;

public record GraphqlData(
	List<BookWithPublishers> getPublishersByBooks
) {
}
