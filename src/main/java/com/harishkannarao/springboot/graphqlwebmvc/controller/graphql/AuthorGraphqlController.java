package com.harishkannarao.springboot.graphqlwebmvc.controller.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.model.Author;
import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
public class AuthorGraphqlController {

	@SchemaMapping(typeName = "Book", field = "authors")
	public List<Author> listAuthors(final Book book) {
		return List.of(new Author(UUID.randomUUID().toString(), "author-" + UUID.randomUUID()));
	}
}
