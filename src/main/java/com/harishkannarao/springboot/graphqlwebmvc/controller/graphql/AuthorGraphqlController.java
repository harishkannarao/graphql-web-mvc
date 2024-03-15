package com.harishkannarao.springboot.graphqlwebmvc.controller.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.dao.AuthorDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.BookAuthorDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.entity.DbEntity;
import com.harishkannarao.springboot.graphqlwebmvc.model.Author;
import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
import com.harishkannarao.springboot.graphqlwebmvc.model.BookAuthor;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class AuthorGraphqlController {

	private final BookAuthorDao bookAuthorDao;
	private final AuthorDao authorDao;

	public AuthorGraphqlController(
		BookAuthorDao bookAuthorDao,
		AuthorDao authorDao) {
		this.bookAuthorDao = bookAuthorDao;
		this.authorDao = authorDao;
	}

	@BatchMapping(typeName = "Book", field = "authors", maxBatchSize = 100)
	public Map<Book, List<Author>> listAuthors(final List<Book> books) {
		List<String> bookIds = books.stream().map(Book::id).toList();
		List<BookAuthor> bookAuthorsList = bookAuthorDao.listByBookIds(bookIds)
			.stream()
			.map(DbEntity::data).toList();
		List<String> authorIds = bookAuthorsList.stream().map(BookAuthor::authorId).toList();

		Map<String, List<BookAuthor>> bookIdAuthorMapping = bookAuthorsList.stream()
			.collect(Collectors.groupingBy(BookAuthor::bookId));
		Map<String, Author> authorIdMap = authorDao.list(authorIds).stream().map(DbEntity::data)
			.collect(Collectors.toUnmodifiableMap(Author::id, author -> author));

		return books.stream()
			.map(book -> {
				List<BookAuthor> bookAuthors = Optional.ofNullable(bookIdAuthorMapping.get(book.id()))
					.orElse(Collections.emptyList());
				List<Author> authors = bookAuthors.stream()
					.map(bookAuthor -> authorIdMap.get(bookAuthor.authorId()))
					.filter(Objects::nonNull)
					.toList();
				return Map.entry(book, authors);
			})
			.collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
	}
}
