package com.harishkannarao.springboot.graphqlwebmvc.service;

import com.harishkannarao.springboot.graphqlwebmvc.dao.BookDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.entity.DbEntity;
import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
import com.harishkannarao.springboot.graphqlwebmvc.model.BookInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class BookService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final BookDao bookDao;

	public BookService(BookDao bookDao) {
		this.bookDao = bookDao;
	}

	@Transactional
	public Optional<Book> createBook(
		BookInput bookInput
	) {
		logger.info("createBook bookInput received as {}", bookInput);
		bookDao.create(new Book(
			bookInput.id(),
			bookInput.name(),
			bookInput.rating(),
			bookInput.isbn(),
			bookInput.publishedDateTime()
		));
		Optional<Book> createdBook = bookDao.get(bookInput.id()).map(DbEntity::data);
		logger.info("createBook bookInput completed for {}", bookInput);
		return createdBook;
	}
}
