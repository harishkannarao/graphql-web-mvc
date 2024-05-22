package com.harishkannarao.springboot.graphqlwebmvc.model.publisher;

import com.harishkannarao.springboot.graphqlwebmvc.client.graphql.dto.BookWithPublishers;

import java.util.List;

public record GetPublishersGqlData(
	List<BookWithPublishers> getPublishersByBooks
) {
}
