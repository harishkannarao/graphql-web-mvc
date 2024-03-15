package com.harishkannarao.springboot.graphqlwebmvc.controller.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.dao.AuthorDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.BookAuthorDao;
import com.harishkannarao.springboot.graphqlwebmvc.dao.entity.DbEntity;
import com.harishkannarao.springboot.graphqlwebmvc.model.Author;
import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
import com.harishkannarao.springboot.graphqlwebmvc.model.BookAuthor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.List;

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

	@SchemaMapping(typeName = "Book", field = "authors")
	public List<Author> listAuthors(final Book book) {
		List<String> authorIds = bookAuthorDao.listByBookIds(List.of(book.id()))
			.stream()
			.map(DbEntity::data)
			.map(BookAuthor::authorId)
			.toList();
		return authorIds.isEmpty()
			? Collections.emptyList()
			: authorDao.list(authorIds).stream().map(DbEntity::data).toList();
	}
}
