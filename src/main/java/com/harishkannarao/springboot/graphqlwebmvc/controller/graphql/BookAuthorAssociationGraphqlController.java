package com.harishkannarao.springboot.graphqlwebmvc.controller.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.dao.AuthorDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.BookAuthorDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.BookDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.entity.DbEntity;
import com.harishkannarao.springboot.graphqlwebmvc.model.Author;
import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
import com.harishkannarao.springboot.graphqlwebmvc.model.BookAuthor;
import com.harishkannarao.springboot.graphqlwebmvc.model.BookInput;
import com.harishkannarao.springboot.graphqlwebmvc.model.CreateBookAuthorResponse;
import com.harishkannarao.springboot.graphqlwebmvc.model.CreateBookResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
public class BookAuthorAssociationGraphqlController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final BookAuthorDao bookAuthorDao;
	private final BookDao bookDao;
	private final AuthorDao authorDao;

	public BookAuthorAssociationGraphqlController(
		BookAuthorDao bookAuthorDao,
		BookDao bookDao,
		AuthorDao authorDao) {
		this.bookAuthorDao = bookAuthorDao;
		this.bookDao = bookDao;
		this.authorDao = authorDao;
	}

	@MutationMapping(name = "associateBookAndAuthor")
	public CreateBookAuthorResponse associateBookAndAuthor(
		@Argument(name = "bookId") String bookId,
		@Argument(name = "authorId") String authorId) {
		logger.info("associateBookAndAuthor received with bookId {} and authorId {}", bookId, authorId);
		bookAuthorDao.create(new BookAuthor(bookId, authorId));
		Optional<DbEntity<Book>> book = bookDao.get(bookId);
		Optional<DbEntity<Author>> author = authorDao.get(authorId);
		logger.info("createBook bookInput completed for bookId {} and authorId {}", bookId, authorId);
		return new CreateBookAuthorResponse(
			book.isPresent() && author.isPresent(),
			book.isPresent() && author.isPresent() ? "success" : "error",
			author.map(DbEntity::data).orElse(null),
			book.map(DbEntity::data).orElse(null)
		);
	}
}
