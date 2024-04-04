package com.harishkannarao.springboot.graphqlwebmvc.controller.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.dao.BookDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.entity.DbEntity;
import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
import com.harishkannarao.springboot.graphqlwebmvc.model.BookInput;
import com.harishkannarao.springboot.graphqlwebmvc.model.CreateBookResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
public class BookGraphqlController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final BookDao bookDao;

	public BookGraphqlController(BookDao bookDao) {
		this.bookDao = bookDao;
	}

	@MutationMapping(name = "createBook")
	public CreateBookResponse createBook(
		@Argument(name = "bookInput") BookInput bookInput) {
		logger.info("createBook bookInput received as {}", bookInput);
		bookDao.create(new Book(bookInput.id(), bookInput.name(), bookInput.rating()));
		Optional<Book> createdBook = bookDao.get(bookInput.id()).map(DbEntity::data);
		return new CreateBookResponse(
			createdBook.isPresent(),
			createdBook.isPresent() ? "success" : "error",
			createdBook.orElseThrow()
		);
	}

	@QueryMapping(name = "listBooks")
	public List<Book> books(
		@Argument(name = "bookIds") List<String> ids) {
		logger.info("listBooks received for ids {}", ids);
		return bookDao.list(ids).stream().map(DbEntity::data).toList();
	}
}
