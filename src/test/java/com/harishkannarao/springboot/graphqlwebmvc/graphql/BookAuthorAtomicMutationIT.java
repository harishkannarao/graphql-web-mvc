package com.harishkannarao.springboot.graphqlwebmvc.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.AbstractBaseIT;
import com.harishkannarao.springboot.graphqlwebmvc.dao.AuthorDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.BookAuthorDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.BookDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.entity.DbEntity;
import com.harishkannarao.springboot.graphqlwebmvc.model.*;
import com.harishkannarao.springboot.graphqlwebmvc.util.JsonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BookAuthorAtomicMutationIT extends AbstractBaseIT {

	private final HttpGraphQlTester httpGraphQlTester;
	private final BookDao bookDao;
	private final AuthorDao authorDao;
	private final BookAuthorDao bookAuthorDao;
	private final JsonUtil jsonUtil;

	@Autowired
	public BookAuthorAtomicMutationIT(HttpGraphQlTester httpGraphQlTester, BookDao bookDao,
																		AuthorDao authorDao,
																		BookAuthorDao bookAuthorDao,
																		JsonUtil jsonUtil) {
		this.httpGraphQlTester = httpGraphQlTester;
		this.bookDao = bookDao;
		this.authorDao = authorDao;
		this.bookAuthorDao = bookAuthorDao;
		this.jsonUtil = jsonUtil;
	}

	@Test
	public void createBookAndAuthor_successfully_creates_and_returns_book_in_atomic_fashion() {
		BookInput bookInput = new BookInput(
			UUID.randomUUID().toString(),
			"book-" + UUID.randomUUID(),
			null,
			"ISBN-2024-04-15-1",
			Optional.empty());
		AuthorInput authorInput = new AuthorInput(
			UUID.randomUUID().toString(),
			"author-" + UUID.randomUUID()
		);

		GraphQlTester.Response response = httpGraphQlTester
			.documentName("mutation/createBookAuthorAtomic")
			.variable("bookInput", bookInput)
			.variable("authorInput", authorInput)
			.variable("bookId", bookInput.id())
			.variable("authorId", authorInput.id())
			.execute();

		response.errors().satisfy(errors -> assertThat(errors).isEmpty());

		Boolean createBookResponse = response
			.path("createBook.success")
			.hasValue()
			.entity(Boolean.class)
			.get();
		assertThat(createBookResponse).isTrue();

		Boolean createAuthorResponse = response
			.path("createAuthor.success")
			.hasValue()
			.entity(Boolean.class)
			.get();
		assertThat(createAuthorResponse).isTrue();

		CreateBookAuthorResponse associateBookAndAuthor = response
			.path("associateBookAndAuthor")
			.hasValue()
			.entity(CreateBookAuthorResponse.class)
			.get();

		assertThat(associateBookAndAuthor.success()).isTrue();
		assertThat(associateBookAndAuthor.message()).isEqualTo("success");
		assertThat(associateBookAndAuthor.book().id()).isEqualTo(bookInput.id());
		assertThat(associateBookAndAuthor.book().name()).isEqualTo(bookInput.name());
		assertThat(associateBookAndAuthor.book().rating()).isEqualTo(bookInput.rating());
		assertThat(associateBookAndAuthor.author().id()).isEqualTo(authorInput.id());
		assertThat(associateBookAndAuthor.author().name()).isEqualTo(authorInput.name());

		List<Author> bookAuthors = response
			.path("associateBookAndAuthor.book.authors")
			.hasValue()
			.entityList(Author.class)
			.get();
		assertThat(bookAuthors)
			.hasSize(1)
			.anySatisfy(author -> assertThat(author.id()).isEqualTo(authorInput.id()));

		List<Book> authorBooks = response
			.path("associateBookAndAuthor.author.books")
			.hasValue()
			.entityList(Book.class)
			.get();
		assertThat(authorBooks)
			.hasSize(1)
			.anySatisfy(book -> assertThat(book.id()).isEqualTo(bookInput.id()));
	}

	@Test
	public void createBookAndAuthor_returns_error_for_blank_author_name() {
		BookInput bookInput = new BookInput(
			UUID.randomUUID().toString(),
			"book-" + UUID.randomUUID(),
			null,
			"ISBN-2024-04-15-1",
			Optional.empty());
		AuthorInput authorInput = new AuthorInput(
			UUID.randomUUID().toString(),
			""
		);

		GraphQlTester.Response response = httpGraphQlTester
			.documentName("mutation/createBookAuthorAtomic")
			.variable("bookInput", bookInput)
			.variable("authorInput", authorInput)
			.variable("bookId", bookInput.id())
			.variable("authorId", authorInput.id())
			.execute();

		response.errors()
			.satisfy(errors -> assertThat(errors)
				.anySatisfy(error -> {
					assertThat(error.getMessage())
						.isEqualTo("/createAuthor/authorInput/name must not be blank");
					assertThat(error.getPath()).isEqualTo("createAuthor");
				}));
	}

	@Test
	public void createBookAndAuthor_returns_error_for_bad_author_name() {
		BookInput bookInput = new BookInput(
			UUID.randomUUID().toString(),
			"book-" + UUID.randomUUID(),
			null,
			"ISBN-2024-04-15-1",
			Optional.empty());
		AuthorInput authorInput = new AuthorInput(
			UUID.randomUUID().toString(),
			"bad-author"
		);

		GraphQlTester.Response response = httpGraphQlTester
			.documentName("mutation/createBookAuthorAtomic")
			.variable("bookInput", bookInput)
			.variable("authorInput", authorInput)
			.variable("bookId", bookInput.id())
			.variable("authorId", authorInput.id())
			.execute();

		// internal error during author creation
		response.errors()
			.satisfy(errors -> assertThat(errors)
				.anySatisfy(error -> {
					assertThat(error.getMessage())
						.contains("INTERNAL_ERROR");
					assertThat(error.getPath()).isEqualTo("createAuthor");
				}));

		response
			.path("createAuthor")
			.valueIsNull();

		Boolean createBookResponse = response
			.path("createBook.success")
			.hasValue()
			.entity(Boolean.class)
			.get();
		assertThat(createBookResponse).isTrue();

		// book should be saved successfully
		Optional<DbEntity<Book>> bookDbEntity = bookDao.get(bookInput.id());
		assertThat(bookDbEntity)
			.isNotEmpty()
			.hasValueSatisfying(value -> assertThat(value.data().id()).isEqualTo(bookInput.id()));

		CreateBookAuthorResponse associateBookAndAuthor = response
			.path("associateBookAndAuthor")
			.hasValue()
			.entity(CreateBookAuthorResponse.class)
			.get();

		assertThat(associateBookAndAuthor.success()).isFalse();
		assertThat(associateBookAndAuthor.message()).isEqualTo("error");
		assertThat(associateBookAndAuthor.book().id()).isEqualTo(bookInput.id());
		assertThat(associateBookAndAuthor.book().name()).isEqualTo(bookInput.name());
		assertThat(associateBookAndAuthor.book().rating()).isEqualTo(bookInput.rating());
		assertThat(associateBookAndAuthor.author()).isNull();

		// association should be saved successfully
		List<DbEntity<BookAuthor>> bookAuthors = bookAuthorDao.listByBookIds(List.of(bookInput.id()));
		assertThat(bookAuthors)
			.hasSize(1)
			.anySatisfy(value -> assertThat(value.data().authorId()).isEqualTo(authorInput.id()));
	}
}
