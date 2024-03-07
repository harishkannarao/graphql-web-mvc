package com.harishkannarao.springboot.graphqlwebmvc.controller.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.model.BookResponseDto;
import com.harishkannarao.springboot.graphqlwebmvc.model.CreateBookRequestDto;
import com.harishkannarao.springboot.graphqlwebmvc.model.CreateBookResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class BookGraphqlController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@MutationMapping(name = "createBook")
	public CreateBookResponseDto createBook(
		@Argument(name = "book") CreateBookRequestDto createBookRequestDto) {
		logger.info("createBook request received as {}", createBookRequestDto);
		return new CreateBookResponseDto(
			true,
			"successfully created",
			new BookResponseDto(
				createBookRequestDto.id(),
				createBookRequestDto.name()
			)
		);
	}
}
