package com.harishkannarao.springboot.graphqlwebmvc.controller.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.dao.BookDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.entity.DbEntity;
import com.harishkannarao.springboot.graphqlwebmvc.loader.AuthorBooksDataLoader;
import com.harishkannarao.springboot.graphqlwebmvc.model.Author;
import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
import com.harishkannarao.springboot.graphqlwebmvc.model.BookInput;
import com.harishkannarao.springboot.graphqlwebmvc.model.CreateBookResponse;
import com.harishkannarao.springboot.graphqlwebmvc.service.BookService;
import org.dataloader.BatchLoaderEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class BookGraphqlController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final BookDao bookDao;
	private final BookService bookService;
	private final AuthorBooksDataLoader authorBooksDataLoader;

	public BookGraphqlController(
		BookDao bookDao, BookService bookService,
		AuthorBooksDataLoader authorBooksDataLoader) {
		this.bookDao = bookDao;
		this.bookService = bookService;
		this.authorBooksDataLoader = authorBooksDataLoader;
	}

	@MutationMapping(name = "createBook")
	public CreateBookResponse createBook(
		@Argument(name = "bookInput") BookInput bookInput) {
		Optional<Book> createdBook = bookService.createBook(bookInput);
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

	@QueryMapping(name = "getBook")
	public Book book(
		@Argument(name = "bookId") String id) {
		logger.info("getBook received for id {}", id);
		return bookDao.get(id).map(DbEntity::data).orElse(null);
	}

	@BatchMapping(typeName = "Author", field = "books")
	public Map<Author, List<Book>> listAuthors(
		Set<Author> authors,
		BatchLoaderEnvironment batchLoaderEnvironment) {
		final var mappedResult = authorBooksDataLoader.apply(authors, batchLoaderEnvironment);
		return mappedResult.entrySet()
			.stream()
			.map(authorListEntry ->
				Map.entry(
					authorListEntry.getKey(),
					authorListEntry.getValue().stream().map(DbEntity::data).toList()
				)
			)
			.collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
	}
}
