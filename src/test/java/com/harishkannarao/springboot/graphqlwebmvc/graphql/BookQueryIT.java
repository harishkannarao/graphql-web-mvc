package com.harishkannarao.springboot.graphqlwebmvc.graphql;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.harishkannarao.springboot.graphqlwebmvc.AbstractBaseIT;
import com.harishkannarao.springboot.graphqlwebmvc.client.graphql.dto.BookWithPublishers;
import com.harishkannarao.springboot.graphqlwebmvc.client.rest.dto.BookWithRetailers;
import com.harishkannarao.springboot.graphqlwebmvc.dao.AuthorDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.BookAuthorDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.BookDao;
import com.harishkannarao.springboot.graphqlwebmvc.model.Author;
import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
import com.harishkannarao.springboot.graphqlwebmvc.model.BookAuthor;
import com.harishkannarao.springboot.graphqlwebmvc.model.Retailer;
import com.harishkannarao.springboot.graphqlwebmvc.model.publisher.GetPublishersGqlData;
import com.harishkannarao.springboot.graphqlwebmvc.model.publisher.GetPublishersGqlResponse;
import com.harishkannarao.springboot.graphqlwebmvc.model.Publisher;
import com.harishkannarao.springboot.graphqlwebmvc.util.FileReaderUtil;
import com.harishkannarao.springboot.graphqlwebmvc.util.JsonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static org.assertj.core.api.Assertions.assertThat;

public class BookQueryIT extends AbstractBaseIT {
	private final HttpGraphQlTester httpGraphQlTester;
	private final BookDao bookDao;
	private final AuthorDao authorDao;
	private final BookAuthorDao bookAuthorDao;
	private final JsonUtil jsonUtil;

	@Autowired
	public BookQueryIT(HttpGraphQlTester httpGraphQlTester,
										 BookDao bookDao,
										 AuthorDao authorDao,
										 BookAuthorDao bookAuthorDao, JsonUtil jsonUtil) {
		this.httpGraphQlTester = httpGraphQlTester;
		this.bookDao = bookDao;
		this.authorDao = authorDao;
		this.bookAuthorDao = bookAuthorDao;
		this.jsonUtil = jsonUtil;
	}

	@Test
	public void listBooks_returnsBooks_withAuthors() {
		Book book1 = new Book(UUID.randomUUID().toString(), "book-1-" + UUID.randomUUID(), BigDecimal.valueOf(3.0), "ISBN-2024-04-15-1", Optional.empty());
		Book book2 = new Book(UUID.randomUUID().toString(), "book-2-" + UUID.randomUUID(), null, "ISBN-2024-04-15-1", Optional.of(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS)));
		Book book3 = new Book(UUID.randomUUID().toString(), "book-3-" + UUID.randomUUID(), null, "ISBN-2024-04-15-1", Optional.empty());
		Book book4 = new Book(UUID.randomUUID().toString(), "book-4-" + UUID.randomUUID(), null, "ISBN-2024-04-15-1", Optional.empty());
		bookDao.create(book1);
		bookDao.create(book2);
		bookDao.create(book3);
		bookDao.create(book4);

		Author author1 = new Author(UUID.randomUUID().toString(), "author-1-" + UUID.randomUUID());
		Author author2 = new Author(UUID.randomUUID().toString(), "author-2-" + UUID.randomUUID());
		Author author3 = new Author(UUID.randomUUID().toString(), "author-3-" + UUID.randomUUID());
		authorDao.create(author1);
		authorDao.create(author2);
		authorDao.create(author3);

		BookAuthor book1Author1 = new BookAuthor(book1.id(), author1.id());
		BookAuthor book1Author2 = new BookAuthor(book1.id(), author2.id());
		BookAuthor book1Author3 = new BookAuthor(book1.id(), author3.id());
		BookAuthor book3Author2 = new BookAuthor(book3.id(), author2.id());
		bookAuthorDao.create(book1Author1);
		bookAuthorDao.create(book1Author2);
		bookAuthorDao.create(book1Author3);
		bookAuthorDao.create(book3Author2);

		GetPublishersGqlResponse getPublishersGqlResponse = new GetPublishersGqlResponse(
			new GetPublishersGqlData(Collections.emptyList()), null);
		String publishersJson = jsonUtil.toJson(getPublishersGqlResponse);
		String expectedQuery = FileReaderUtil.readFile("graphql-documents/publisher/getPublishersByBooks.graphql");
		wireMock.register(
			post(urlEqualTo("/graphql"))
				.withRequestBody(matchingJsonPath("$.query", equalTo(expectedQuery)))
				.willReturn(WireMock.okJson(publishersJson))
		);

		wireMock.register(
			post(urlEqualTo("/rest"))
				.willReturn(okJson(jsonUtil.toJson(Collections.emptyList())))
		);

		GraphQlTester.Response result = httpGraphQlTester
			.documentName("query/queryListBooks")
			.variable("bookIds", List.of(book1.id(), book2.id(), book3.id()))
			.variable("authorLimit", 2)
			.execute();

		result.errors()
			.satisfy(errors -> assertThat(errors).isEmpty());

		List<Book> booksResult = result
			.path("listBooks")
			.hasValue()
			.entityList(Book.class)
			.get();

		assertThat(booksResult)
			.hasSize(3)
			.contains(book1, book2, book3);

		List<Author> book1Authors = result
			.path("listBooks[0].authors")
			.hasValue()
			.entityList(Author.class)
			.get();

		assertThat(book1Authors)
			.containsExactlyInAnyOrder(author3, author2);

		List<Book> author3Books = result
			.path("listBooks[0].authors[0].books")
			.hasValue()
			.entityList(Book.class)
			.get();

		assertThat(author3Books)
			.containsExactlyInAnyOrder(book1);

		List<Author> book2Authors = result
			.path("listBooks[1].authors")
			.hasValue()
			.entityList(Author.class)
			.get();

		assertThat(book2Authors)
			.isEmpty();

		List<Author> book3Authors = result
			.path("listBooks[2].authors")
			.hasValue()
			.entityList(Author.class)
			.get();

		assertThat(book3Authors)
			.containsExactlyInAnyOrder(author2);
	}

	@Test
	public void listBooks_returnsBooks_withPublishers() {
		Book book1 = new Book(UUID.randomUUID().toString(), "book-1-" + UUID.randomUUID(), BigDecimal.valueOf(3.0), "ISBN-2024-04-15-1", Optional.empty());
		Book book2 = new Book(UUID.randomUUID().toString(), "book-2-" + UUID.randomUUID(), null, "ISBN-2024-04-15-1", Optional.of(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS)));
		Book book3 = new Book(UUID.randomUUID().toString(), "book-3-" + UUID.randomUUID(), null, "ISBN-2024-04-15-1", Optional.empty());
		Book book4 = new Book(UUID.randomUUID().toString(), "book-4-" + UUID.randomUUID(), null, "ISBN-2024-04-15-1", Optional.empty());
		bookDao.create(book1);
		bookDao.create(book2);
		bookDao.create(book3);
		bookDao.create(book4);

		Publisher publisher1 = new Publisher(UUID.randomUUID(), "pub-name-1");
		Publisher publisher2 = new Publisher(UUID.randomUUID(), "pub-name-2");
		Publisher publisher3 = new Publisher(UUID.randomUUID(), "pub-name-3");

		BookWithPublishers book1Publishers = new BookWithPublishers(book1.id(), List.of(publisher1, publisher2));
		BookWithPublishers book2Publishers = new BookWithPublishers(book2.id(), List.of(publisher3));
		List<BookWithPublishers> publishers = List.of(book1Publishers, book2Publishers);
		GetPublishersGqlResponse getPublishersGqlResponse = new GetPublishersGqlResponse(new GetPublishersGqlData(publishers), null);
		String publishersJson = jsonUtil.toJson(getPublishersGqlResponse);

		String expectedQuery = FileReaderUtil.readFile("graphql-documents/publisher/getPublishersByBooks.graphql");
		String expectedBookIds = jsonUtil.toJson(List.of(book2.id(), book1.id(), book3.id()));
		wireMock.register(
			post(urlEqualTo("/graphql"))
				.withRequestBody(matchingJsonPath("$.query", equalTo(expectedQuery)))
				.withRequestBody(matchingJsonPath("$.variables.bookIds", equalToJson(expectedBookIds, true, false)))
				.willReturn(WireMock.okJson(publishersJson))
		);

		wireMock.register(
			post(urlEqualTo("/rest"))
				.willReturn(okJson(jsonUtil.toJson(Collections.emptyList())))
		);

		GraphQlTester.Response result = httpGraphQlTester
			.documentName("query/queryListBooks")
			.variable("bookIds", List.of(book1.id(), book2.id(), book3.id()))
			.variable("authorLimit", 2)
			.execute();

		result.errors()
			.satisfy(errors -> assertThat(errors).isEmpty());

		List<Book> booksResult = result
			.path("listBooks")
			.hasValue()
			.entityList(Book.class)
			.get();

		assertThat(booksResult)
			.containsExactlyInAnyOrder(book1, book2, book3);

		List<Publisher> publishersOfBook1 = result
			.path("listBooks[0].publishers")
			.hasValue()
			.entityList(Publisher.class)
			.get();

		assertThat(publishersOfBook1)
			.containsExactlyInAnyOrder(publisher1, publisher2);

		List<Publisher> publishersOfBook2 = result
			.path("listBooks[1].publishers")
			.hasValue()
			.entityList(Publisher.class)
			.get();

		assertThat(publishersOfBook2)
			.containsExactlyInAnyOrder(publisher3);

		List<Publisher> publishersOfBook3 = result
			.path("listBooks[2].publishers")
			.hasValue()
			.entityList(Publisher.class)
			.get();

		assertThat(publishersOfBook3).isEmpty();
	}

	@Test
	public void listBooks_returnsBooks_withRetailers() {
		Book book1 = new Book(UUID.randomUUID().toString(), "book-1-" + UUID.randomUUID(), BigDecimal.valueOf(3.0), "ISBN-2024-04-15-1", Optional.empty());
		Book book2 = new Book(UUID.randomUUID().toString(), "book-2-" + UUID.randomUUID(), null, "ISBN-2024-04-15-1", Optional.of(OffsetDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS)));
		Book book3 = new Book(UUID.randomUUID().toString(), "book-3-" + UUID.randomUUID(), null, "ISBN-2024-04-15-1", Optional.empty());
		Book book4 = new Book(UUID.randomUUID().toString(), "book-4-" + UUID.randomUUID(), null, "ISBN-2024-04-15-1", Optional.empty());
		bookDao.create(book1);
		bookDao.create(book2);
		bookDao.create(book3);
		bookDao.create(book4);

		Retailer retailer1 = new Retailer(UUID.randomUUID(), "retailer-1");
		Retailer retailer2 = new Retailer(UUID.randomUUID(), "retailer-2");
		Retailer retailer3 = new Retailer(UUID.randomUUID(), "retailer-3");

		BookWithRetailers book1Retailers = new BookWithRetailers(book1.id(), List.of(retailer1, retailer2));
		BookWithRetailers book2Retailers = new BookWithRetailers(book2.id(), List.of(retailer2, retailer3));

		Set<String> input = Set.of(book1.id(), book2.id(), book3.id());

		wireMock.register(
			post(urlEqualTo("/rest"))
				.withRequestBody(equalToJson(jsonUtil.toJson(input), true, false))
				.willReturn(okJson(jsonUtil.toJson(List.of(book1Retailers, book2Retailers))))
		);

		GetPublishersGqlResponse getPublishersGqlResponse = new GetPublishersGqlResponse(
			new GetPublishersGqlData(Collections.emptyList()), null);
		String emptyPublishers = jsonUtil.toJson(getPublishersGqlResponse);

		String expectedQuery = FileReaderUtil.readFile("graphql-documents/publisher/getPublishersByBooks.graphql");
		wireMock.register(
			post(urlEqualTo("/graphql"))
				.withRequestBody(matchingJsonPath("$.query", equalTo(expectedQuery)))
				.willReturn(WireMock.okJson(emptyPublishers))
		);

		GraphQlTester.Response result = httpGraphQlTester
			.documentName("query/queryListBooks")
			.variable("bookIds", List.of(book1.id(), book2.id(), book3.id()))
			.variable("authorLimit", 2)
			.execute();

		result.errors()
			.satisfy(errors -> assertThat(errors).isEmpty());

		List<Book> booksResult = result
			.path("listBooks")
			.hasValue()
			.entityList(Book.class)
			.get();

		assertThat(booksResult)
			.containsExactlyInAnyOrder(book1, book2, book3);

		List<Retailer> retailersOfBook1 = result
			.path("listBooks[0].retailers")
			.hasValue()
			.entityList(Retailer.class)
			.get();

		assertThat(retailersOfBook1)
			.containsExactlyInAnyOrder(retailer1, retailer2);

		List<Retailer> retailersOfBook2 = result
			.path("listBooks[1].retailers")
			.hasValue()
			.entityList(Retailer.class)
			.get();

		assertThat(retailersOfBook2)
			.containsExactlyInAnyOrder(retailer2, retailer3);

		List<Retailer> retailersOfBook3 = result
			.path("listBooks[2].retailers")
			.hasValue()
			.entityList(Retailer.class)
			.get();

		assertThat(retailersOfBook3).isEmpty();

		List<LoggedRequest> loggedRequests = wireMock.find(postRequestedFor(urlEqualTo("/rest")));

		List<String[]> receivedBody = loggedRequests.stream().map(LoggedRequest::getBodyAsString)
			.map(s -> jsonUtil.fromJson(s, String[].class))
			.toList();
		assertThat(receivedBody)
			.hasSize(1)
			.anySatisfy(bookIds ->
				assertThat(bookIds)
					.hasSize(3)
					.contains(book1.id(), book2.id(), book3.id()));
	}

	@Test
	public void listBooks_returnsError_whenPublisherGraphlService_fails() {
		Book book1 = new Book(UUID.randomUUID().toString(), "book-1-" + UUID.randomUUID(), BigDecimal.valueOf(3.0), "ISBN-2024-04-15-1", Optional.empty());
		bookDao.create(book1);

		String expectedQuery = FileReaderUtil.readFile("graphql-documents/publisher/getPublishersByBooks.graphql");
		String expectedBookIds = jsonUtil.toJson(List.of(book1.id()));
		wireMock.register(
			post(urlEqualTo("/graphql"))
				.withRequestBody(matchingJsonPath("$.query", equalTo(expectedQuery)))
				.withRequestBody(matchingJsonPath("$.variables.bookIds", equalToJson(expectedBookIds, true, false)))
				.willReturn(WireMock.serverError().withBody("MY INTERNAL SERVER ERROR"))
		);

		GraphQlTester.Response result = httpGraphQlTester
			.documentName("query/queryListBooks")
			.variable("bookIds", List.of(book1.id()))
			.variable("authorLimit", 2)
			.execute();

		result.errors()
			.satisfy(errors -> assertThat(errors)
				.anySatisfy(error -> {
					assertThat(error.getMessage())
						.contains("INTERNAL_ERROR");
					assertThat(error.getPath()).isEqualTo("listBooks[0].publishers");
				}));

		result
			.path("listBooks")
			.pathDoesNotExist();
	}

	@Test
	public void listBooks_returnsError_forInvalidIsbn() {
		Book book1 = new Book(UUID.randomUUID().toString(), "book-1-" + UUID.randomUUID(), BigDecimal.valueOf(3.0), "invalid-isbn", Optional.empty());

		bookDao.create(book1);

		GraphQlTester.Response result = httpGraphQlTester
			.documentName("query/queryListBooks")
			.variable("bookIds", List.of(book1.id()))
			.variable("authorLimit", 2)
			.execute();

		result.errors()
			.satisfy(errors -> assertThat(errors)
				.anySatisfy(error -> {
					assertThat(error.getMessage())
						.isEqualTo("Can't serialize value (/listBooks[0]/isbn) : Unable to accept a value into the 'ISBN' scalar.  It does not match the regular expressions.");
					assertThat(error.getPath()).isEqualTo("listBooks[0].isbn");
				}));
	}

	@Test
	public void getBook_returnsBook_byId_withAuthor() {
		Book book = new Book(UUID.randomUUID().toString(), "book-" + UUID.randomUUID(), BigDecimal.valueOf(3.0), "ISBN-2024-04-15-1", Optional.empty());
		bookDao.create(book);

		Author author = new Author(UUID.randomUUID().toString(), "author-" + UUID.randomUUID());
		authorDao.create(author);

		BookAuthor bookAuthor = new BookAuthor(book.id(), author.id());
		bookAuthorDao.create(bookAuthor);

		GraphQlTester.Response result = httpGraphQlTester
			.documentName("query/queryGetBook")
			.variable("bookId", book.id())
			.execute();

		result.errors()
			.satisfy(errors -> assertThat(errors).isEmpty());

		Book bookResult = result
			.path("getBook")
			.hasValue()
			.entity(Book.class)
			.get();

		assertThat(bookResult)
			.isEqualTo(book);

		List<Author> bookAuthorsResult = result
			.path("getBook.authors")
			.hasValue()
			.entityList(Author.class)
			.get();

		assertThat(bookAuthorsResult)
			.containsExactlyInAnyOrder(author);
	}

	@Test
	public void getBook_returnsNull_ifBook_doNot_exists() {
		GraphQlTester.Response result = httpGraphQlTester
			.documentName("query/queryGetBook")
			.variable("bookId", UUID.randomUUID().toString())
			.execute();

		result.errors()
			.satisfy(errors -> assertThat(errors).isEmpty());

		result
			.path("getBook")
			.valueIsNull();
	}
}
