package com.harishkannarao.springboot.graphqlwebmvc.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.AbstractBaseIT;
import com.harishkannarao.springboot.graphqlwebmvc.dao.AuthorDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.BookAuthorDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.BookDao;
import com.harishkannarao.springboot.graphqlwebmvc.model.Author;
import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
import com.harishkannarao.springboot.graphqlwebmvc.model.BookAuthor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class AuthorQueryIT extends AbstractBaseIT {

	private final HttpGraphQlTester httpGraphQlTester;
	private final BookDao bookDao;
	private final AuthorDao authorDao;
	private final BookAuthorDao bookAuthorDao;

	@Autowired
	public AuthorQueryIT(
		HttpGraphQlTester httpGraphQlTester,
		BookDao bookDao,
		AuthorDao authorDao,
		BookAuthorDao bookAuthorDao) {
		this.httpGraphQlTester = httpGraphQlTester;
		this.bookDao = bookDao;
		this.authorDao = authorDao;
		this.bookAuthorDao = bookAuthorDao;
	}

	@Test
	public void listAuthors_returns_authors_with_books() {
		Book book1 = new Book(UUID.randomUUID().toString(), "book-1-" + UUID.randomUUID(), BigDecimal.valueOf(3.0));
		Book book2 = new Book(UUID.randomUUID().toString(), "book-2-" + UUID.randomUUID(), null);
		Book book3 = new Book(UUID.randomUUID().toString(), "book-3-" + UUID.randomUUID(), null);
		Book book4 = new Book(UUID.randomUUID().toString(), "book-4-" + UUID.randomUUID(), null);
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

		GraphQlTester.Response result = httpGraphQlTester
			.documentName("query/queryListAuthors")
			.variable("authorIds", List.of(author1.id(), author2.id(), author3.id()))
			.execute();
	}
}
