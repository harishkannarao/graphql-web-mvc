package com.harishkannarao.springboot.graphqlwebmvc.controller.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.client.graphql.PublisherGraphqlClient;
import com.harishkannarao.springboot.graphqlwebmvc.client.graphql.dto.BookWithPublishers;
import com.harishkannarao.springboot.graphqlwebmvc.client.graphql.dto.PublisherQueryResult;
import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
import com.harishkannarao.springboot.graphqlwebmvc.model.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

@Controller
public class BookPublisherController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private final PublisherGraphqlClient publisherGraphqlClient;

	public BookPublisherController(
		PublisherGraphqlClient publisherGraphqlClient) {
		this.publisherGraphqlClient = publisherGraphqlClient;
	}

	@BatchMapping(typeName = "Book", field = "publishers")
	public Map<Book, List<Publisher>> listPublishers(
		Set<Book> books) {
		try {
			Set<String> bookIds = books.stream().map(Book::id).collect(Collectors.toUnmodifiableSet());
			PublisherQueryResult publisherQueryResult = publisherGraphqlClient.queryPublishers(bookIds).join();
			if (!publisherQueryResult.errors().isEmpty()) {
				publisherQueryResult.errors().forEach(error -> log.error(error.toString()));
				throw new RuntimeException("Publishers lookup by book ids falied");
			}
			Map<String, List<Publisher>> publishersByBookId = publisherQueryResult.data().stream()
				.collect(Collectors.toUnmodifiableMap(BookWithPublishers::bookId, BookWithPublishers::publishers));
			return books.stream()
				.collect(Collectors.toUnmodifiableMap(
					book -> book,
					book -> publishersByBookId.getOrDefault(book.id(), Collections.emptyList())
				));
		} catch (CompletionException e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}
}
