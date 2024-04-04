package com.harishkannarao.springboot.graphqlwebmvc.unit.loader;

import com.harishkannarao.springboot.graphqlwebmvc.dao.AuthorDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.BookAuthorDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.entity.DbEntity;
import com.harishkannarao.springboot.graphqlwebmvc.loader.BookAuthorsDataLoader;
import com.harishkannarao.springboot.graphqlwebmvc.model.Author;
import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
import com.harishkannarao.springboot.graphqlwebmvc.model.BookAuthor;
import org.assertj.core.api.Assertions;
import org.dataloader.BatchLoaderEnvironment;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;

import java.lang.annotation.Target;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked", "rawtypes"})
public class BookAuthorsDataLoaderTest {

	private final BookAuthorDao bookAuthorDao = mock(BookAuthorDao.class);
	private final AuthorDao authorDao = mock(AuthorDao.class);
	private final BatchLoaderEnvironment batchLoaderEnvironment = mock(BatchLoaderEnvironment.class);
	private final BookAuthorsDataLoader subject =
		new BookAuthorsDataLoader(bookAuthorDao, authorDao);

	@Test
	public void test_author_list_returned_for_given_books() {
		Book book1 = new Book(UUID.randomUUID().toString(), "book-1-" + UUID.randomUUID(), BigDecimal.valueOf(3.0));
		Book book2 = new Book(UUID.randomUUID().toString(), "book-2-" + UUID.randomUUID(), null);
		Book book3 = new Book(UUID.randomUUID().toString(), "book-3-" + UUID.randomUUID(), null);

		DbEntity<Author> author1 = new DbEntity<>(new Author(UUID.randomUUID().toString(), "author-1-" + UUID.randomUUID()), Instant.now(), Instant.now());
		DbEntity<Author> author2 = new DbEntity<>(new Author(UUID.randomUUID().toString(), "author-2-" + UUID.randomUUID()), Instant.now(), Instant.now());
		DbEntity<Author> author3 = new DbEntity<>(new Author(UUID.randomUUID().toString(), "author-3-" + UUID.randomUUID()), Instant.now(), Instant.now());

		DbEntity<BookAuthor> book1Author1 = new DbEntity<>(new BookAuthor(book1.id(), author1.data().id()), Instant.now(), Instant.now());
		DbEntity<BookAuthor> book1Author2 = new DbEntity<>(new BookAuthor(book1.id(), author2.data().id()), Instant.now(), Instant.now());
		DbEntity<BookAuthor> book1Author3 = new DbEntity<>(new BookAuthor(book1.id(), author3.data().id()), Instant.now(), Instant.now());
		DbEntity<BookAuthor> book3Author2 = new DbEntity<>(new BookAuthor(book3.id(), author2.data().id()), Instant.now(), Instant.now());

		when(bookAuthorDao.listByBookIds(anyList()))
			.thenReturn(List.of(book1Author1, book1Author2, book1Author3, book3Author2));

		when(authorDao.list(anyList()))
			.thenReturn(List.of(author1, author2, author3));

		Mono<Map<Book, List<DbEntity<Author>>>> monoResult = subject.apply(Set.of(book1, book2, book3), batchLoaderEnvironment);
		Map<Book, List<DbEntity<Author>>> result = monoResult.block();

		assertThat(result).hasSize(3);
		assertThat(result.get(book1))
			.hasSize(3)
			.contains(author1, author2, author3);
		assertThat(result.get(book2))
			.isEmpty();
		assertThat(result.get(book3))
			.hasSize(1)
			.contains(author2);

		ArgumentCaptor<List<String>> bookAuthorsCaptor = ArgumentCaptor.forClass((Class) List.class);
		verify(bookAuthorDao).listByBookIds(bookAuthorsCaptor.capture());
		assertThat(bookAuthorsCaptor.getValue())
			.hasSize(3)
			.contains(book1.id(), book2.id(), book3.id());

		ArgumentCaptor<List<String>> authorsCaptor = ArgumentCaptor.forClass((Class) List.class);
		verify(authorDao).list(authorsCaptor.capture());
		assertThat(authorsCaptor.getValue())
			.hasSize(3)
			.contains(author1.data().id(), author2.data().id(), author3.data().id());
	}
}
