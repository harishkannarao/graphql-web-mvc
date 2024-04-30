package com.harishkannarao.springboot.graphqlwebmvc.controller.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
import com.harishkannarao.springboot.graphqlwebmvc.model.Publisher;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class BookPublisherController {

	@BatchMapping(typeName = "Book", field = "publishers")
	public Map<Book, List<Publisher>> listPublishers(
		Set<Book> books) {
		return books.stream()
			.map(book -> Map.entry(book, Collections.<Publisher>emptyList()))
			.collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
	}
}
