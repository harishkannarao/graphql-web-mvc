package com.harishkannarao.springboot.graphqlwebmvc.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.AbstractBaseIT;
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
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.harishkannarao.springboot.graphqlwebmvc.util.AuthorizationTokenConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

public class BookAuthorAtomicMutationIT extends AbstractBaseIT {

	private final HttpGraphQlTester httpGraphQlTester;
	private final BookDao bookDao;
	private final BookAuthorDao bookAuthorDao;

	@Autowired
	public BookAuthorAtomicMutationIT(
		HttpGraphQlTester httpGraphQlTester,
		BookDao bookDao,
		BookAuthorDao bookAuthorDao) {
		this.httpGraphQlTester = httpGraphQlTester;
		this.bookDao = bookDao;
		this.bookAuthorDao = bookAuthorDao;
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
			.mutate()
			.header(HttpHeaders.AUTHORIZATION, BEARER_ADMIN_TOKEN)
			.build()
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
	public void createBookAndAuthor_returns_forbidden_error_for_non_admin_user() {
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
			.mutate()
			.header(HttpHeaders.AUTHORIZATION, BEARER_USER_TOKEN)
			.build()
			.documentName("mutation/createBookAuthorAtomic")
			.variable("bookInput", bookInput)
			.variable("authorInput", authorInput)
			.variable("bookId", bookInput.id())
			.variable("authorId", authorInput.id())
			.execute();

		response
			.errors()
			.satisfy(errors -> assertThat(errors)
				.anySatisfy(error -> {
					assertThat(error.getMessage()).contains("Forbidden");
					assertThat(error.getPath()).isEqualTo("associateBookAndAuthor");
				}))
			.path("associateBookAndAuthor")
			.valueIsNull()
			.path("createBook")
			.hasValue()
			.path("createAuthor")
			.hasValue();
	}

	@Test
	public void createBookAndAuthor_returns_unauthorized_error_for_invalid_token() {
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
			.mutate()
			.header(HttpHeaders.AUTHORIZATION, BEARER_INVALID_TOKEN)
			.build()
			.documentName("mutation/createBookAuthorAtomic")
			.variable("bookInput", bookInput)
			.variable("authorInput", authorInput)
			.variable("bookId", bookInput.id())
			.variable("authorId", authorInput.id())
			.execute();

		response
			.errors()
			.satisfy(errors -> assertThat(errors)
				.anySatisfy(error -> {
					assertThat(error.getMessage()).contains("Unauthorized");
					assertThat(error.getPath()).isEqualTo("associateBookAndAuthor");
				}))
			.path("associateBookAndAuthor")
			.valueIsNull()
			.path("createBook")
			.hasValue()
			.path("createAuthor")
			.hasValue();
	}

	@Test
	public void createBookAndAuthor_returns_unauthorized_error_for_missing_token() {
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

		response
			.errors()
			.satisfy(errors -> assertThat(errors)
				.anySatisfy(error -> {
					assertThat(error.getMessage()).contains("Unauthorized");
					assertThat(error.getPath()).isEqualTo("associateBookAndAuthor");
				}))
			.path("associateBookAndAuthor")
			.valueIsNull()
			.path("createBook")
			.hasValue()
			.path("createAuthor")
			.hasValue();
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
			.mutate()
			.header(HttpHeaders.AUTHORIZATION, BEARER_ADMIN_TOKEN)
			.build()
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

		// association should not be saved
		List<DbEntity<BookAuthor>> bookAuthors = bookAuthorDao.listByBookIds(List.of(bookInput.id()));
		assertThat(bookAuthors).isEmpty();
	}
}
