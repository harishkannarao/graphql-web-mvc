package com.harishkannarao.springboot.graphqlwebmvc.graphql;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.harishkannarao.springboot.graphqlwebmvc.AbstractBaseIT;
import com.harishkannarao.springboot.graphqlwebmvc.client.graphql.dto.BookWithPublishers;
import com.harishkannarao.springboot.graphqlwebmvc.dao.AuthorDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.BookAuthorDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.BookDao;
import com.harishkannarao.springboot.graphqlwebmvc.model.Author;
import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
import com.harishkannarao.springboot.graphqlwebmvc.model.BookAuthor;
import com.harishkannarao.springboot.graphqlwebmvc.model.publisher.PublisherGqlData;
import com.harishkannarao.springboot.graphqlwebmvc.model.publisher.PublisherGqlResponse;
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

		PublisherGqlResponse publisherGqlResponse = new PublisherGqlResponse(
			new PublisherGqlData(Collections.emptyList()), null);
		String publishersJson = jsonUtil.toJson(publisherGqlResponse);
		String expectedQuery = FileReaderUtil.readFile("graphql-documents/publisher/getPublishersByBooks.graphql");
		wireMock.register(
			post(urlEqualTo("/graphql"))
				.withRequestBody(matchingJsonPath("$.query", equalTo(expectedQuery)))
				.willReturn(WireMock.okJson(publishersJson))
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

		Publisher publisher1 = new Publisher("pub-id-1", "pub-name-1");
		Publisher publisher2 = new Publisher("pub-id-2", "pub-name-2");
		Publisher publisher3 = new Publisher("pub-id-3", "pub-name-3");

		BookWithPublishers book1Publishers = new BookWithPublishers(book1.id(), List.of(publisher1, publisher2));
		BookWithPublishers book2Publishers = new BookWithPublishers(book2.id(), List.of(publisher3));
		List<BookWithPublishers> publishers = List.of(book1Publishers, book2Publishers);
		PublisherGqlResponse publisherGqlResponse = new PublisherGqlResponse(new PublisherGqlData(publishers), null);
		String publishersJson = jsonUtil.toJson(publisherGqlResponse);

		String expectedQuery = FileReaderUtil.readFile("graphql-documents/publisher/getPublishersByBooks.graphql");
		String expectedBookIds = jsonUtil.toJson(List.of(book2.id(), book1.id(), book3.id()));
		wireMock.register(
			post(urlEqualTo("/graphql"))
				.withRequestBody(matchingJsonPath("$.query", equalTo(expectedQuery)))
				.withRequestBody(matchingJsonPath("$.variables.bookIds", equalToJson(expectedBookIds, true, false)))
				.willReturn(WireMock.okJson(publishersJson))
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
}
