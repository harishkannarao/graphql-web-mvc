package com.harishkannarao.springboot.graphqlwebmvc.graphql;

import com.harishkannarao.springboot.graphqlwebmvc.AbstractBaseIT;
import com.harishkannarao.springboot.graphqlwebmvc.model.CreateBookRequest;
import com.harishkannarao.springboot.graphqlwebmvc.model.CreateBookResponse;
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
		CreateBookRequest createBookRequest = new CreateBookRequest(
			UUID.randomUUID().toString(),
			"book-" + UUID.randomUUID()
		);
		GraphQlTester.Response response = httpGraphQlTester
			.documentName("mutation/createBook")
			.variable("book", createBookRequest)
			.execute();

		response.errors().satisfy(errors -> assertThat(errors).isEmpty());

		CreateBookResponse createBookResponse = response
			.path("createBook")
			.hasValue()
			.entity(CreateBookResponse.class)
			.get();

		assertThat(createBookResponse.success()).isTrue();
		assertThat(createBookResponse.message()).isEqualTo("success");
		assertThat(createBookResponse.book().id()).isEqualTo(createBookRequest.id());
		assertThat(createBookResponse.book().name()).isEqualTo(createBookRequest.name());
	}

	@Test
	public void createBook_successfully_creates_and_does_not_return_created_book_and_message() {
		CreateBookRequest createBookRequest = new CreateBookRequest(
			UUID.randomUUID().toString(),
			"book-" + UUID.randomUUID()
		);
		GraphQlTester.Response response = httpGraphQlTester
			.documentName("mutation/createBook")
			.variable("book", createBookRequest)
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
		CreateBookRequest createBookRequest = new CreateBookRequest(
			UUID.randomUUID().toString(),
			"book-" + UUID.randomUUID()
		);
		GraphQlTester.Response successResponse = httpGraphQlTester
			.documentName("mutation/createBook")
			.variable("book", createBookRequest)
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
			.variable("book", createBookRequest)
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
