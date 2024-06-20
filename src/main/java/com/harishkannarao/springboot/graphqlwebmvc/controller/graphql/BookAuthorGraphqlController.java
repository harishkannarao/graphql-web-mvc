package com.harishkannarao.springboot.graphqlwebmvc.controller.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.model.AuthorInput;
import com.harishkannarao.springboot.graphqlwebmvc.model.BookInput;
import com.harishkannarao.springboot.graphqlwebmvc.model.CreateBookAuthorResponse;
import com.harishkannarao.springboot.graphqlwebmvc.service.BookAuthorService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

@Controller
public class BookAuthorGraphqlController {

	private final BookAuthorService bookAuthorService;

	public BookAuthorGraphqlController(
		BookAuthorService bookAuthorService) {
		this.bookAuthorService = bookAuthorService;
	}

	@MutationMapping(name = "associateBookAndAuthor")
	@Secured("ROLE_ROOT_ADMIN")
	public CreateBookAuthorResponse associateBookAndAuthor(
		@Argument(name = "bookId") String bookId,
		@Argument(name = "authorId") String authorId) {
		return bookAuthorService.associateBookAndAuthor(bookId, authorId);
	}

	@MutationMapping(name = "createBookWithAuthor")
	public CreateBookAuthorResponse createBookWithAuthor(
		@Argument(name = "bookInput") BookInput bookInput,
		@Argument(name = "authorInput") AuthorInput authorInput) {
		return bookAuthorService.createBookWithAuthor(bookInput, authorInput);
	}
}
