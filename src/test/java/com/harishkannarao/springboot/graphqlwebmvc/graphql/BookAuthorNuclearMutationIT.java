package com.harishkannarao.springboot.graphqlwebmvc.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.AbstractBaseIT;
import com.harishkannarao.springboot.graphqlwebmvc.dao.AuthorDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.BookAuthorDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.BookDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.entity.DbEntity;
import com.harishkannarao.springboot.graphqlwebmvc.model.Author;
import com.harishkannarao.springboot.graphqlwebmvc.model.AuthorInput;
import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
import com.harishkannarao.springboot.graphqlwebmvc.model.BookAuthor;
import com.harishkannarao.springboot.graphqlwebmvc.model.BookInput;
import com.harishkannarao.springboot.graphqlwebmvc.model.CreateBookAuthorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BookAuthorNuclearMutationIT extends AbstractBaseIT {

	private final HttpGraphQlTester httpGraphQlTester;
	private final BookDao bookDao;
	private final AuthorDao authorDao;
	private final BookAuthorDao bookAuthorDao;

	@Autowired
	public BookAuthorNuclearMutationIT(
		HttpGraphQlTester httpGraphQlTester,
		BookDao bookDao, AuthorDao authorDao,
		BookAuthorDao bookAuthorDao) {
		this.httpGraphQlTester = httpGraphQlTester;
		this.bookDao = bookDao;
		this.authorDao = authorDao;
		this.bookAuthorDao = bookAuthorDao;
	}

	@Test
	public void createBookWithAuthor_successfully_creates_and_returns_book_in_nuclear_fashion() {
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
			.documentName("mutation/createBookAuthorNuclear")
			.variable("bookInput", bookInput)
			.variable("authorInput", authorInput)
			.execute();

		response.errors().satisfy(errors -> assertThat(errors).isEmpty());

		CreateBookAuthorResponse createBookWithAuthor = response
			.path("createBookWithAuthor")
			.hasValue()
			.entity(CreateBookAuthorResponse.class)
			.get();

		assertThat(createBookWithAuthor.success()).isTrue();
		assertThat(createBookWithAuthor.message()).isEqualTo("success");
		assertThat(createBookWithAuthor.book().id()).isEqualTo(bookInput.id());
		assertThat(createBookWithAuthor.book().name()).isEqualTo(bookInput.name());
		assertThat(createBookWithAuthor.book().rating()).isEqualTo(bookInput.rating());
		assertThat(createBookWithAuthor.author().id()).isEqualTo(authorInput.id());
		assertThat(createBookWithAuthor.author().name()).isEqualTo(authorInput.name());

		List<Author> bookAuthors = response
			.path("createBookWithAuthor.book.authors")
			.hasValue()
			.entityList(Author.class)
			.get();
		assertThat(bookAuthors)
			.hasSize(1)
			.anySatisfy(author -> assertThat(author.id()).isEqualTo(authorInput.id()));

		List<Book> authorBooks = response
			.path("createBookWithAuthor.author.books")
			.hasValue()
			.entityList(Book.class)
			.get();
		assertThat(authorBooks)
			.hasSize(1)
			.anySatisfy(book -> assertThat(book.id()).isEqualTo(bookInput.id()));
	}

	@Test
	public void createBookWithAuthor_is_idempotent() {
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

		GraphQlTester.Response firstResponse = httpGraphQlTester
			.documentName("mutation/createBookAuthorNuclear")
			.variable("bookInput", bookInput)
			.variable("authorInput", authorInput)
			.execute();

		firstResponse.errors().satisfy(errors -> assertThat(errors).isEmpty());

		CreateBookAuthorResponse firstCall = firstResponse
			.path("createBookWithAuthor")
			.hasValue()
			.entity(CreateBookAuthorResponse.class)
			.get();

		assertThat(firstCall.success()).isTrue();
		assertThat(firstCall.message()).isEqualTo("success");
		assertThat(firstCall.book().id()).isEqualTo(bookInput.id());
		assertThat(firstCall.author().id()).isEqualTo(authorInput.id());

		GraphQlTester.Response secondResponse = httpGraphQlTester
			.documentName("mutation/createBookAuthorNuclear")
			.variable("bookInput", bookInput)
			.variable("authorInput", authorInput)
			.execute();

		secondResponse.errors().satisfy(errors -> assertThat(errors).isEmpty());

		CreateBookAuthorResponse secondCall = secondResponse
			.path("createBookWithAuthor")
			.hasValue()
			.entity(CreateBookAuthorResponse.class)
			.get();

		assertThat(secondCall.success()).isTrue();
		assertThat(secondCall.message()).isEqualTo("success");
		assertThat(secondCall.book().id()).isEqualTo(bookInput.id());
		assertThat(secondCall.author().id()).isEqualTo(authorInput.id());
	}

	@Test
	public void createBookWithAuthor_returns_error_for_blank_author_name() {
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
			.documentName("mutation/createBookAuthorNuclear")
			.variable("bookInput", bookInput)
			.variable("authorInput", authorInput)
			.execute();

		response.errors()
			.satisfy(errors -> assertThat(errors)
				.anySatisfy(error -> {
					assertThat(error.getMessage())
						.isEqualTo("/createBookWithAuthor/authorInput/name must not be blank");
					assertThat(error.getPath()).isEqualTo("createBookWithAuthor");
				}));
	}

	@Test
	public void createBookWithAuthor_returns_error_for_bad_author_name() {
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
			.documentName("mutation/createBookAuthorNuclear")
			.variable("bookInput", bookInput)
			.variable("authorInput", authorInput)
			.execute();

		// internal error during author creation
		response.errors()
			.satisfy(errors -> assertThat(errors)
				.anySatisfy(error -> {
					assertThat(error.getMessage())
						.contains("INTERNAL_ERROR");
					assertThat(error.getPath()).isEqualTo("createBookWithAuthor");
				}));

		response
			.path("createBookWithAuthor")
			.valueIsNull();

		// book should not be saved
		Optional<DbEntity<Book>> bookDbEntity = bookDao.get(bookInput.id());
		assertThat(bookDbEntity).isEmpty();

		// author should not be saved
		Optional<DbEntity<Author>> authorDbEntity = authorDao.get(authorInput.id());
		assertThat(authorDbEntity).isEmpty();

		// association should not be saved
		List<DbEntity<BookAuthor>> bookAuthors = bookAuthorDao.listByBookIds(List.of(bookInput.id()));
		assertThat(bookAuthors).isEmpty();
	}
}
