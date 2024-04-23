package com.harishkannarao.springboot.graphqlwebmvc.controller.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.dao.AuthorDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.BookAuthorDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.BookDao;
import com.harishkannarao.springboot.graphqlwebmvc.model.CreateBookAuthorResponse;
import com.harishkannarao.springboot.graphqlwebmvc.service.BookAuthorService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class BookAuthorGraphqlController {

	private final BookAuthorService bookAuthorService;

	public BookAuthorGraphqlController(
		BookAuthorDao bookAuthorDao,
		BookDao bookDao,
		AuthorDao authorDao, BookAuthorService bookAuthorService) {
		this.bookAuthorService = bookAuthorService;
	}

	@MutationMapping(name = "associateBookAndAuthor")
	public CreateBookAuthorResponse associateBookAndAuthor(
		@Argument(name = "bookId") String bookId,
		@Argument(name = "authorId") String authorId) {
		return bookAuthorService.associateBookAndAuthor(bookId, authorId);
	}
}
