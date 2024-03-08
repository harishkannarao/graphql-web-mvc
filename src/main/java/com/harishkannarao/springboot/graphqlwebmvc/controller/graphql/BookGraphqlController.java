package com.harishkannarao.springboot.graphqlwebmvc.controller.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.model.Book;
import com.harishkannarao.springboot.graphqlwebmvc.model.CreateBookReq;
import com.harishkannarao.springboot.graphqlwebmvc.model.CreateBookRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class BookGraphqlController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@MutationMapping(name = "createBook")
	public CreateBookRes createBook(
		@Argument(name = "book") CreateBookReq createBookReq) {
		logger.info("createBook request received as {}", createBookReq);
		return new CreateBookRes(
			true,
			"successfully created",
			new Book(
				createBookReq.id(),
				createBookReq.name()
			)
		);
	}
}
