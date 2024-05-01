package com.harishkannarao.springboot.graphqlwebmvc.controller.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
import com.harishkannarao.springboot.graphqlwebmvc.model.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class BookPublisherController {

	private final HttpGraphQlClient httpGraphQlClient;

	public BookPublisherController(
		HttpGraphQlClient httpGraphQlClient,
		@Value("${third-party.publisher-service.graphql-url}") String publisherServiceUrl) {
		this.httpGraphQlClient = httpGraphQlClient.mutate().url(publisherServiceUrl).build();
	}

	@BatchMapping(typeName = "Book", field = "publishers")
	public Map<Book, List<Publisher>> listPublishers(
		Set<Book> books) {
		return books.stream()
			.map(book -> Map.entry(book, Collections.<Publisher>emptyList()))
			.collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
	}
}
