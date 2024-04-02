package com.harishkannarao.springboot.graphqlwebmvc.controller.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.dao.BookDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.entity.DbEntity;
import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
import com.harishkannarao.springboot.graphqlwebmvc.model.BookInput;
import com.harishkannarao.springboot.graphqlwebmvc.model.CreateBookResponse;
import graphql.GraphQLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.Optional;

import static com.harishkannarao.springboot.graphqlwebmvc.util.Constants.RESPONSE_AUTHOR_LIMIT;

@Controller
public class BookGraphqlController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final BookDao bookDao;

	public BookGraphqlController(BookDao bookDao) {
		this.bookDao = bookDao;
	}

	@MutationMapping(name = "createBook")
	public CreateBookResponse createBook(
		@Argument(name = "bookInput") BookInput bookInput,
		@Argument(name = RESPONSE_AUTHOR_LIMIT) Integer resAuthorLimit,
		GraphQLContext graphQLContext) {
		logger.info("createBook bookInput received as {}", bookInput);
		graphQLContext.put(RESPONSE_AUTHOR_LIMIT, resAuthorLimit);
		bookDao.create(new Book(bookInput.id(), bookInput.name(), bookInput.rating()));
		Optional<Book> createdBook = bookDao.get(bookInput.id()).map(DbEntity::data);
		return new CreateBookResponse(
			createdBook.isPresent(),
			createdBook.isPresent() ? "success" : "error",
			createdBook.orElseThrow()
		);
	}
}
