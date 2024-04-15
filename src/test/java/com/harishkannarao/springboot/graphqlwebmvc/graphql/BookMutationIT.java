package com.harishkannarao.springboot.graphqlwebmvc.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.AbstractBaseIT;
import com.harishkannarao.springboot.graphqlwebmvc.dao.AuthorDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.BookAuthorDao;
import com.harishkannarao.springboot.graphqlwebmvc.model.Author;
import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
import com.harishkannarao.springboot.graphqlwebmvc.model.BookAuthor;
import com.harishkannarao.springboot.graphqlwebmvc.model.BookInput;
import com.harishkannarao.springboot.graphqlwebmvc.model.CreateBookResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BookMutationIT extends AbstractBaseIT {

	private final HttpGraphQlTester httpGraphQlTester;
	private final AuthorDao authorDao;
	private final BookAuthorDao bookAuthorDao;

	@Autowired
	public BookMutationIT(HttpGraphQlTester httpGraphQlTester,
												AuthorDao authorDao,
												BookAuthorDao bookAuthorDao) {
		this.httpGraphQlTester = httpGraphQlTester;
		this.authorDao = authorDao;
		this.bookAuthorDao = bookAuthorDao;
	}

	@Test
	public void createBook_successfully_creates_and_returns_book() {
		BookInput bookInput = new BookInput(
			UUID.randomUUID().toString(),
			"book-" + UUID.randomUUID(),
			null, "ISBN-2024-04-15-1");
		GraphQlTester.Response response = httpGraphQlTester
			.documentName("mutation/createBook")
			.variable("bookInput", bookInput)
			.execute();

		response.errors().satisfy(errors -> assertThat(errors).isEmpty());

		CreateBookResponse createBookResponse = response
			.path("createBook")
			.hasValue()
			.entity(CreateBookResponse.class)
			.get();

		assertThat(createBookResponse.success()).isTrue();
		assertThat(createBookResponse.message()).isEqualTo("success");
		assertThat(createBookResponse.book().id()).isEqualTo(bookInput.id());
		assertThat(createBookResponse.book().name()).isEqualTo(bookInput.name());
		assertThat(createBookResponse.book().rating()).isEqualTo(bookInput.rating());

		response
			.path("createBook.book.authors")
			.pathDoesNotExist();
	}

	@Test
	public void createBook_successfully_creates_and_returns_book_with_authors_with_default_limit() {
		var book = new Book(UUID.randomUUID().toString(), "book-" + UUID.randomUUID(), null, "ISBN-2024-04-15-1");

		var author1 = new Author(UUID.randomUUID().toString(), "author-" + UUID.randomUUID());
		authorDao.create(author1);
		var author2 = new Author(UUID.randomUUID().toString(), "author-" + UUID.randomUUID());
		authorDao.create(author2);
		var author3 = new Author(UUID.randomUUID().toString(), "author-" + UUID.randomUUID());
		authorDao.create(author3);

		var bookAuthor1 = new BookAuthor(book.id(), author1.id());
		var bookAuthor2 = new BookAuthor(book.id(), author2.id());
		var bookAuthor3 = new BookAuthor(book.id(), author3.id());
		bookAuthorDao.create(bookAuthor1);
		bookAuthorDao.create(bookAuthor2);
		bookAuthorDao.create(bookAuthor3);

		BookInput bookInput = new BookInput(
			book.id(),
			book.name(),
			BigDecimal.valueOf(2.25), "ISBN-2024-04-15-1");
		GraphQlTester.Response response = httpGraphQlTester
			.documentName("mutation/createBook")
			.variable("bookInput", bookInput)
			.variable("includeAuthors", Boolean.TRUE)
			.execute();

		response.errors().satisfy(errors -> assertThat(errors).isEmpty());

		CreateBookResponse createBookResponse = response
			.path("createBook")
			.hasValue()
			.entity(CreateBookResponse.class)
			.get();

		assertThat(createBookResponse.success()).isTrue();
		assertThat(createBookResponse.message()).isEqualTo("success");
		assertThat(createBookResponse.book().id()).isEqualTo(bookInput.id());
		assertThat(createBookResponse.book().name()).isEqualTo(bookInput.name());
		assertThat(createBookResponse.book().rating()).isEqualTo(bookInput.rating());

		List<Author> authors = response
			.path("createBook.book.authors")
			.hasValue()
			.entityList(Author.class)
			.get();

		assertThat(authors)
			.hasSize(2)
			.contains(author3, author2);
	}

	@Test
	public void createBook_successfully_creates_and_returns_book_with_authors_with_limit() {
		var book = new Book(UUID.randomUUID().toString(), "book-" + UUID.randomUUID(), null, "ISBN-2024-04-15-1");

		var author1 = new Author(UUID.randomUUID().toString(), "author-" + UUID.randomUUID());
		authorDao.create(author1);
		var author2 = new Author(UUID.randomUUID().toString(), "author-" + UUID.randomUUID());
		authorDao.create(author2);

		var bookAuthor1 = new BookAuthor(book.id(), author1.id());
		var bookAuthor2 = new BookAuthor(book.id(), author2.id());
		bookAuthorDao.create(bookAuthor1);
		bookAuthorDao.create(bookAuthor2);

		BookInput bookInput = new BookInput(
			book.id(),
			book.name(),
			BigDecimal.valueOf(2.25), "ISBN-2024-04-15-1");
		GraphQlTester.Response response = httpGraphQlTester
			.documentName("mutation/createBook")
			.variable("bookInput", bookInput)
			.variable("includeAuthors", Boolean.TRUE)
			.variable("responseAuthorLimit", 1)
			.execute();

		response.errors().satisfy(errors -> assertThat(errors).isEmpty());

		CreateBookResponse createBookResponse = response
			.path("createBook")
			.hasValue()
			.entity(CreateBookResponse.class)
			.get();

		assertThat(createBookResponse.success()).isTrue();
		assertThat(createBookResponse.message()).isEqualTo("success");
		assertThat(createBookResponse.book().id()).isEqualTo(bookInput.id());
		assertThat(createBookResponse.book().name()).isEqualTo(bookInput.name());
		assertThat(createBookResponse.book().rating()).isEqualTo(bookInput.rating());

		List<Author> authors = response
			.path("createBook.book.authors")
			.hasValue()
			.entityList(Author.class)
			.get();

		assertThat(authors)
			.hasSize(1)
			.contains(author2);
	}

	@Test
	public void createBook_returns_validation_error_for_invalid_author_limit() {
		var book = new Book(UUID.randomUUID().toString(), "book-" + UUID.randomUUID(), null, "ISBN-2024-04-15-1");

		BookInput bookInput = new BookInput(
			book.id(),
			book.name(),
			BigDecimal.valueOf(2.25), "ISBN-2024-04-15-1");
		GraphQlTester.Response response = httpGraphQlTester
			.documentName("mutation/createBook")
			.variable("bookInput", bookInput)
			.variable("includeAuthors", Boolean.TRUE)
			.variable("responseAuthorLimit", 101)
			.execute();

		response.errors()
			.satisfy(errors -> assertThat(errors)
				.anySatisfy(error -> {
					assertThat(error.getMessage()).isEqualTo("/createBook/book/authors/limit must be less than or equal to 100");
					assertThat(error.getPath()).isEqualTo("createBook.book.authors");
				}));
	}

	@Test
	public void createBook_returns_validation_error_for_invalid_isbn() {
		BookInput bookInput = new BookInput(
			UUID.randomUUID().toString(),
			"book-" + UUID.randomUUID(),
			BigDecimal.valueOf(2.25),
			"invalid-isbn");

		GraphQlTester.Response response = httpGraphQlTester
			.documentName("mutation/createBook")
			.variable("bookInput", bookInput)
			.variable("includeAuthors", Boolean.FALSE)
			.execute();

		response.errors()
			.satisfy(errors -> assertThat(errors)
				.anySatisfy(error -> {
					assertThat(error.getMessage()).isEqualTo("Variable 'bookInput' has an invalid value: Unable to accept a value into the 'ISBN' scalar.  It does not match the regular expressions.");
					assertThat(error.getPath()).isEqualTo("");
				}));
	}

	@Test
	public void createBook_successfully_creates_and_does_not_return_created_book_and_message() {
		BookInput bookInput = new BookInput(
			UUID.randomUUID().toString(),
			"book-" + UUID.randomUUID(),
			null, "ISBN-2024-04-15-1");
		GraphQlTester.Response response = httpGraphQlTester
			.documentName("mutation/createBook")
			.variable("bookInput", bookInput)
			.variable("skipMessage", Boolean.TRUE)
			.variable("returnCreatedBook", Boolean.FALSE)
			.execute();

		response.errors().satisfy(errors -> assertThat(errors).isEmpty());

		CreateBookResponse createBookResponse = response
			.path("createBook")
			.hasValue()
			.entity(CreateBookResponse.class)
			.get();

		assertThat(createBookResponse.success()).isTrue();
		assertThat(createBookResponse.message()).isNull();
		assertThat(createBookResponse.book()).isNull();
	}

	@Test
	public void createBook_fails_with_duplicate_key_error() {
		BookInput bookInput = new BookInput(
			UUID.randomUUID().toString(),
			"book-" + UUID.randomUUID(),
			null, "ISBN-2024-04-15-1");
		GraphQlTester.Response successResponse = httpGraphQlTester
			.documentName("mutation/createBook")
			.variable("bookInput", bookInput)
			.execute();

		successResponse.errors().satisfy(errors -> assertThat(errors).isEmpty());

		CreateBookResponse createBookResponse = successResponse
			.path("createBook")
			.hasValue()
			.entity(CreateBookResponse.class)
			.get();

		assertThat(createBookResponse.success()).isTrue();
		assertThat(createBookResponse.message()).isEqualTo("success");

		GraphQlTester.Response errorResponse = httpGraphQlTester
			.documentName("mutation/createBook")
			.variable("bookInput", bookInput)
			.execute();

		errorResponse
			.errors()
			.satisfy(errors -> assertThat(errors)
				.anySatisfy(error -> {
					assertThat(error.getMessage()).contains("Duplicate key / entity already exists");
					assertThat(error.getPath()).isEqualTo("createBook");
				}))
			.path("createBook")
			.valueIsNull();
	}
}
