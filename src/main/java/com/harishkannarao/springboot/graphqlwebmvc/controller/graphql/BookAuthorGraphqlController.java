package com.harishkannarao.springboot.graphqlwebmvc.controller.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.model.CreateBookAuthorResponse;
import com.harishkannarao.springboot.graphqlwebmvc.service.BookAuthorService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class BookAuthorGraphqlController {

	private final BookAuthorService bookAuthorService;

	public BookAuthorGraphqlController(
		BookAuthorService bookAuthorService) {
		this.bookAuthorService = bookAuthorService;
	}

	@MutationMapping(name = "associateBookAndAuthor")
	public CreateBookAuthorResponse associateBookAndAuthor(
		@Argument(name = "bookId") String bookId,
		@Argument(name = "authorId") String authorId) {
		return bookAuthorService.associateBookAndAuthor(bookId, authorId);
	}
}
