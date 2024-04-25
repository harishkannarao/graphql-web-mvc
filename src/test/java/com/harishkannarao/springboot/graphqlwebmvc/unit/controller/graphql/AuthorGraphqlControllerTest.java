package com.harishkannarao.springboot.graphqlwebmvc.unit.controller.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.controller.graphql.AuthorGraphqlController;
import com.harishkannarao.springboot.graphqlwebmvc.dao.AuthorDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.entity.DbEntity;
import com.harishkannarao.springboot.graphqlwebmvc.model.Author;
import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
import com.harishkannarao.springboot.graphqlwebmvc.service.AuthorService;
import org.dataloader.DataLoader;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class AuthorGraphqlControllerTest {
	private final AuthorDao authorDao = mock(AuthorDao.class);
	private final AuthorService authorService = mock(AuthorService.class);
	private final DataLoader<Book, List<DbEntity<Author>>> bookListDataLoader = mock(DataLoader.class);
	private final AuthorGraphqlController subject =
		new AuthorGraphqlController(authorDao, authorService);

	@Test
	public void listAuthors_returns_authors_for_given_book() throws Exception {
		Book book = new Book(UUID.randomUUID().toString(), "book-1-" + UUID.randomUUID(), BigDecimal.valueOf(3.0), "ISBN-2024-04-15-1", Optional.empty());
		Author author1 = new Author(UUID.randomUUID().toString(), "author-1-" + UUID.randomUUID());
		Author author2 = new Author(UUID.randomUUID().toString(), "author-2-" + UUID.randomUUID());
		Author author3 = new Author(UUID.randomUUID().toString(), "author-2-" + UUID.randomUUID());
		DbEntity<Author> authorDbEntity1 = new DbEntity<>(author1, Instant.now(), Instant.now());
		DbEntity<Author> authorDbEntity2 = new DbEntity<>(author2, Instant.now(), Instant.now());
		DbEntity<Author> authorDbEntity3 = new DbEntity<>(author3, Instant.now(), Instant.now());

		List<DbEntity<Author>> authorsInDb = List.of(authorDbEntity1, authorDbEntity2, authorDbEntity3);

		when(bookListDataLoader.load(book)).thenReturn(CompletableFuture.supplyAsync(() -> authorsInDb));

		CompletableFuture<List<Author>> result = subject.listAuthors(2, book, bookListDataLoader);
		List<Author> authors = result.get();

		assertThat(authors)
			.hasSize(2)
			.containsExactly(author3, author2);
	}

	@Test
	public void listAuthors_returns_empty_for_given_book() throws Exception {
		Book book = new Book(UUID.randomUUID().toString(), "book-1-" + UUID.randomUUID(), BigDecimal.valueOf(3.0), "ISBN-2024-04-15-1", Optional.empty());

		when(bookListDataLoader.load(book)).thenReturn(CompletableFuture.supplyAsync(Collections::emptyList));

		CompletableFuture<List<Author>> result = subject.listAuthors(2, book, bookListDataLoader);
		List<Author> authors = result.get();

		assertThat(authors)
			.isEmpty();
	}

}
