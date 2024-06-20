package com.harishkannarao.springboot.graphqlwebmvc.controller.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.client.graphql.PublisherGraphqlClient;
import com.harishkannarao.springboot.graphqlwebmvc.client.graphql.dto.BookWithPublishers;
import com.harishkannarao.springboot.graphqlwebmvc.client.graphql.dto.CreatePublisherMutationResult;
import com.harishkannarao.springboot.graphqlwebmvc.client.graphql.dto.PublisherQueryResult;
import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
import com.harishkannarao.springboot.graphqlwebmvc.model.CreatePublishersResponse;
import com.harishkannarao.springboot.graphqlwebmvc.model.Publisher;
import com.harishkannarao.springboot.graphqlwebmvc.model.PublisherInput;
import com.harishkannarao.springboot.graphqlwebmvc.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
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
		Set<Book> books,
		@ContextValue(name = Constants.X_REQUEST_ID) final String requestId) {
		Set<String> bookIds = books.stream().map(Book::id).collect(Collectors.toUnmodifiableSet());
		PublisherQueryResult publisherQueryResult = publisherGraphqlClient.queryPublishers(bookIds, requestId).join();
		if (!publisherQueryResult.errors().isEmpty()) {
			publisherQueryResult.errors().forEach(error -> log.error(error.toString()));
			throw new RuntimeException("Publishers lookup by book ids falied " + publisherQueryResult.errors());
		}
		Map<String, List<Publisher>> publishersByBookId = publisherQueryResult.data().stream()
			.collect(Collectors.toUnmodifiableMap(BookWithPublishers::bookId, BookWithPublishers::publishers));
		return books.stream()
			.collect(Collectors.toUnmodifiableMap(
				book -> book,
				book -> publishersByBookId.getOrDefault(book.id(), Collections.emptyList())
			));
	}

	@MutationMapping(name = "createPublishers")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public CompletableFuture<CreatePublishersResponse> createPublishers(
		@Argument(name = "publishers") Set<PublisherInput> publisherInputs,
		@ContextValue(name = Constants.X_REQUEST_ID) final String requestId) {
		CompletableFuture<CreatePublisherMutationResult> createResponse = publisherGraphqlClient
			.createPublishers(publisherInputs, requestId);
		return createResponse
			.thenApply(result -> new CreatePublishersResponse(
				result.data(),
				result.data() ? "success" : "failure")
			);
	}
}
