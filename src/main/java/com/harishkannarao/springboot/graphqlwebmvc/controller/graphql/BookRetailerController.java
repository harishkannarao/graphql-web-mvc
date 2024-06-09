package com.harishkannarao.springboot.graphqlwebmvc.controller.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.client.graphql.PublisherGraphqlClient;
import com.harishkannarao.springboot.graphqlwebmvc.client.graphql.dto.BookWithPublishers;
import com.harishkannarao.springboot.graphqlwebmvc.client.graphql.dto.PublisherQueryResult;
import com.harishkannarao.springboot.graphqlwebmvc.client.rest.RetailerRestClient;
import com.harishkannarao.springboot.graphqlwebmvc.client.rest.dto.BookWithRetailers;
import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
import com.harishkannarao.springboot.graphqlwebmvc.model.Publisher;
import com.harishkannarao.springboot.graphqlwebmvc.model.Retailer;
import com.harishkannarao.springboot.graphqlwebmvc.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class BookRetailerController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private final RetailerRestClient retailerRestClient;

	public BookRetailerController(
		RetailerRestClient retailerRestClient) {
		this.retailerRestClient = retailerRestClient;
	}

	@BatchMapping(typeName = "Book", field = "retailers")
	public Map<Book, List<Retailer>> listRetailers(Set<Book> books) {
		Set<String> bookIds = books.stream().map(Book::id).collect(Collectors.toUnmodifiableSet());
		List<BookWithRetailers> bookWithRetailers = retailerRestClient.lookupRetailers(bookIds);
		Map<String, List<Retailer>> bookRetailersMap = bookWithRetailers.stream()
			.collect(Collectors.toUnmodifiableMap(BookWithRetailers::bookId, BookWithRetailers::retailers));
		return books.stream()
			.collect(Collectors.toUnmodifiableMap(
				book -> book,
				book -> bookRetailersMap.getOrDefault(book.id(), Collections.emptyList())
			));
	}
}
