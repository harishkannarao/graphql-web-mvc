package com.harishkannarao.springboot.graphqlwebmvc.controller.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.dao.AuthorDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.BookAuthorDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.entity.DbEntity;
import com.harishkannarao.springboot.graphqlwebmvc.model.Author;
import com.harishkannarao.springboot.graphqlwebmvc.model.AuthorInput;
import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
import com.harishkannarao.springboot.graphqlwebmvc.model.BookInput;
import com.harishkannarao.springboot.graphqlwebmvc.model.CreateAuthorResponse;
import com.harishkannarao.springboot.graphqlwebmvc.model.CreateBookResponse;
import org.dataloader.DataLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Controller
public class AuthorGraphqlController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final BookAuthorDao bookAuthorDao;
	private final AuthorDao authorDao;

	public AuthorGraphqlController(
		BookAuthorDao bookAuthorDao,
		AuthorDao authorDao) {
		this.bookAuthorDao = bookAuthorDao;
		this.authorDao = authorDao;
	}

	@MutationMapping(name = "createAuthor")
	public CreateAuthorResponse createAuthor(
		@Argument(name = "authorInput") AuthorInput authorInput) {
		logger.info("createAuthor authorInput received as {}", authorInput);
		if (authorInput.name().equals("bad-author")) {
			throw new RuntimeException("Bad Author");
		}
		authorDao.create(new Author(authorInput.id(), authorInput.name()));
		Optional<Author> createdAuthor = authorDao.get(authorInput.id()).map(DbEntity::data);
		logger.info("createAuthor authorInput completed for {}", authorInput);
		return new CreateAuthorResponse(
			createdAuthor.isPresent(),
			createdAuthor.isPresent() ? "success" : "error",
			createdAuthor.orElseThrow()
		);
	}

	@QueryMapping(name = "listAuthors")
	public List<Author> authors(
		@Argument(name = "authorIds") List<String> ids) {
		logger.info("listBooks received for ids {}", ids);
		return authorDao.list(ids).stream()
			.sorted((o1, o2) -> o2.createdTime().compareTo(o1.createdTime()))
			.map(DbEntity::data).toList();
	}

	@SchemaMapping(typeName = "Book", field = "authors")
	public CompletableFuture<List<Author>> listAuthors(
		@Argument(name = "limit") Integer authorLimit,
		Book book,
		DataLoader<Book, List<DbEntity<Author>>> bookAuthorsLoader
	) {
		return bookAuthorsLoader.load(book)
			.thenApply(dbEntities -> dbEntities
				.stream()
				.sorted((o1, o2) -> o2.createdTime().compareTo(o1.createdTime()))
				.limit(authorLimit)
				.map(DbEntity::data)
				.toList()
			);
	}
}
