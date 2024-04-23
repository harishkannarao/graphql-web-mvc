package com.harishkannarao.springboot.graphqlwebmvc.service;

import com.harishkannarao.springboot.graphqlwebmvc.dao.AuthorDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.BookAuthorDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.BookDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.entity.DbEntity;
import com.harishkannarao.springboot.graphqlwebmvc.model.Author;
import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
import com.harishkannarao.springboot.graphqlwebmvc.model.BookAuthor;
import com.harishkannarao.springboot.graphqlwebmvc.model.CreateBookAuthorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class BookAuthorService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final BookAuthorDao bookAuthorDao;
	private final BookDao bookDao;
	private final AuthorDao authorDao;

	public BookAuthorService(BookAuthorDao bookAuthorDao, BookDao bookDao, AuthorDao authorDao) {
		this.bookAuthorDao = bookAuthorDao;
		this.bookDao = bookDao;
		this.authorDao = authorDao;
	}

	@Transactional
	public CreateBookAuthorResponse associateBookAndAuthor(
		String bookId,
		String authorId) {
		logger.info("associateBookAndAuthor received with bookId {} and authorId {}", bookId, authorId);

		Optional<DbEntity<Book>> book = bookDao.get(bookId);
		Optional<DbEntity<Author>> author = authorDao.get(authorId);
		if (book.isPresent() && author.isPresent()) {
			bookAuthorDao.create(new BookAuthor(bookId, authorId));
			logger.info("associateBookAndAuthor completed for bookId {} and authorId {}", bookId, authorId);
		} else {
			logger.info("associateBookAndAuthor unsuccessful for bookId {} and authorId {}", bookId, authorId);
		}
		return new CreateBookAuthorResponse(
			book.isPresent() && author.isPresent(),
			book.isPresent() && author.isPresent() ? "success" : "error",
			author.map(DbEntity::data).orElse(null),
			book.map(DbEntity::data).orElse(null)
		);
	}
}
