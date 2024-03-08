package com.harishkannarao.springboot.graphqlwebmvc.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.AbstractBaseIT;
import com.harishkannarao.springboot.graphqlwebmvc.model.CreateBookReq;
import com.harishkannarao.springboot.graphqlwebmvc.model.CreateBookRes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BookQueryMutationIT extends AbstractBaseIT {

	private final HttpGraphQlTester httpGraphQlTester;

	@Autowired
	public BookQueryMutationIT(HttpGraphQlTester httpGraphQlTester) {
		this.httpGraphQlTester = httpGraphQlTester;
	}

	@Test
	public void createBook_successfully_creates_and_returns_book() {
		CreateBookReq createBookRequest = new CreateBookReq(
			UUID.randomUUID().toString(),
			"book-" + UUID.randomUUID()
		);
		GraphQlTester.Response response = httpGraphQlTester
			.documentName("mutation/createBook")
			.variable("book", createBookRequest)
			.execute();

		response.errors().satisfy(errors -> assertThat(errors).isEmpty());

		CreateBookRes createBookResponse = response
			.path("createBook")
			.hasValue()
			.entity(CreateBookRes.class)
			.get();

		assertThat(createBookResponse.success()).isTrue();
		assertThat(createBookResponse.message()).isEqualTo("successfully created");
		assertThat(createBookResponse.book().id()).isEqualTo(createBookRequest.id());
		assertThat(createBookResponse.book().name()).isEqualTo(createBookRequest.name());
	}

	@Test
	public void createBook_successfully_creates_and_does_not_return_created_book() {
		CreateBookReq createBookRequest = new CreateBookReq(
			UUID.randomUUID().toString(),
			"book-" + UUID.randomUUID()
		);
		GraphQlTester.Response response = httpGraphQlTester
			.documentName("mutation/createBook")
			.variable("book", createBookRequest)
			.variable("returnCreatedBook", Boolean.FALSE)
			.execute();

		response.errors().satisfy(errors -> assertThat(errors).isEmpty());

		CreateBookRes createBookResponse = response
			.path("createBook")
			.hasValue()
			.entity(CreateBookRes.class)
			.get();

		assertThat(createBookResponse.success()).isTrue();
		assertThat(createBookResponse.message()).isEqualTo("successfully created");
		assertThat(createBookResponse.book()).isNull();
	}
}
