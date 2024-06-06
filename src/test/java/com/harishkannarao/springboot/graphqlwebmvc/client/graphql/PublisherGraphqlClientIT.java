package com.harishkannarao.springboot.graphqlwebmvc.client.graphql;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.harishkannarao.springboot.graphqlwebmvc.AbstractBaseIT;
import com.harishkannarao.springboot.graphqlwebmvc.client.graphql.dto.BookWithPublishers;
import com.harishkannarao.springboot.graphqlwebmvc.client.graphql.dto.CreatePublisherMutationResult;
import com.harishkannarao.springboot.graphqlwebmvc.client.graphql.dto.PublisherQueryResult;
import com.harishkannarao.springboot.graphqlwebmvc.model.PublisherInput;
import com.harishkannarao.springboot.graphqlwebmvc.model.publisher.CreatePublishersGqlData;
import com.harishkannarao.springboot.graphqlwebmvc.model.publisher.CreatePublishersGqlRequest;
import com.harishkannarao.springboot.graphqlwebmvc.model.publisher.CreatePublishersGqlResponse;
import com.harishkannarao.springboot.graphqlwebmvc.model.publisher.GetPublishersGqlData;
import com.harishkannarao.springboot.graphqlwebmvc.model.publisher.PublisherGqlError;
import com.harishkannarao.springboot.graphqlwebmvc.model.publisher.GetPublishersGqlRequest;
import com.harishkannarao.springboot.graphqlwebmvc.model.publisher.GetPublishersGqlResponse;
import com.harishkannarao.springboot.graphqlwebmvc.model.Publisher;
import com.harishkannarao.springboot.graphqlwebmvc.util.FileReaderUtil;
import com.harishkannarao.springboot.graphqlwebmvc.util.JsonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletionException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PublisherGraphqlClientIT extends AbstractBaseIT {

	private final String requestId = UUID.randomUUID().toString();
	private final PublisherGraphqlClient publisherGraphqlClient;
	private final JsonUtil jsonUtil;

	@Autowired
	public PublisherGraphqlClientIT(
		PublisherGraphqlClient publisherGraphqlClient, JsonUtil jsonUtil) {
		this.publisherGraphqlClient = publisherGraphqlClient;
		this.jsonUtil = jsonUtil;
	}

	@Test
	public void queryPublishers_returnsPublishers_byBookIds() {
		String bookId1 = "book-id-1";
		String bookId2 = "book-id-2";
		Set<String> input = Set.of(bookId1, bookId2);
		Publisher publisher1 = new Publisher(UUID.randomUUID(), "pub-name-1");
		Publisher publisher2 = new Publisher(UUID.randomUUID(), "pub-name-2");
		Publisher publisher3 = new Publisher(UUID.randomUUID(), "pub-name-3");

		BookWithPublishers book1Publishers = new BookWithPublishers(bookId1, List.of(publisher1, publisher2));
		BookWithPublishers book2Publishers = new BookWithPublishers(bookId1, List.of(publisher3));
		List<BookWithPublishers> publishers = List.of(book1Publishers, book2Publishers);
		GetPublishersGqlResponse getPublishersGqlResponse = new GetPublishersGqlResponse(new GetPublishersGqlData(publishers), null);
		String publishersJson = jsonUtil.toJson(getPublishersGqlResponse);

		String expectedQuery = FileReaderUtil.readFile("graphql-documents/publisher/getPublishersByBooks.graphql");
		String expectedBookIds = jsonUtil.toJson(List.of(bookId2, bookId1));
		wireMock.register(
			post(urlEqualTo("/graphql"))
				.withRequestBody(matchingJsonPath("$.query", equalTo(expectedQuery)))
				.withRequestBody(matchingJsonPath("$.variables.bookIds", equalToJson(expectedBookIds, true, false)))
				.willReturn(WireMock.okJson(publishersJson))
		);

		PublisherQueryResult result = publisherGraphqlClient.queryPublishers(input, requestId).join();
		assertThat(result.errors()).isEmpty();
		assertThat(result.data())
			.contains(book1Publishers)
			.contains(book2Publishers)
			.hasSize(2);

		List<LoggedRequest> loggedRequests = wireMock.find(postRequestedFor(urlEqualTo("/graphql"))
			.withRequestBody(matchingJsonPath("$.query", equalTo(expectedQuery))));

		List<GetPublishersGqlRequest> receivedBody = loggedRequests.stream().map(LoggedRequest::getBodyAsString)
			.map(s -> jsonUtil.fromJson(s, GetPublishersGqlRequest.class))
			.toList();
		assertThat(receivedBody)
			.hasSize(1)
			.anySatisfy(getPublishersGqlRequest ->
				assertThat(getPublishersGqlRequest.variables().bookIds())
					.hasSize(2)
					.contains(bookId1, bookId2));
	}

	@Test
	public void queryPublishers_returnsEmpty_whenFieldIsNull() {
		GetPublishersGqlResponse getPublishersGqlResponse = new GetPublishersGqlResponse(new GetPublishersGqlData(null), null);
		String publishersJson = jsonUtil.toJson(getPublishersGqlResponse);

		String expectedQuery = FileReaderUtil.readFile("graphql-documents/publisher/getPublishersByBooks.graphql");
		String expectedBookIds = jsonUtil.toJson(List.of());
		wireMock.register(
			post(urlEqualTo("/graphql"))
				.withRequestBody(matchingJsonPath("$.query", equalTo(expectedQuery)))
				.withRequestBody(matchingJsonPath("$.variables.bookIds", equalToJson(expectedBookIds, true, false)))
				.willReturn(WireMock.okJson(publishersJson))
		);

		PublisherQueryResult result = publisherGraphqlClient.queryPublishers(Set.of(), requestId).join();
		assertThat(result.errors()).isEmpty();
		assertThat(result.data()).isEmpty();
	}

	@Test
	public void queryPublishers_returnsErrors_onErrorFromRemoteService() {
		List<PublisherGqlError> errors = List.of(
			new PublisherGqlError("artificial-error", List.of("getPublishersByBooks"))
		);
		GetPublishersGqlResponse getPublishersGqlResponse = new GetPublishersGqlResponse(new GetPublishersGqlData(null), errors);
		String publishersJson = jsonUtil.toJson(getPublishersGqlResponse);

		String expectedQuery = FileReaderUtil.readFile("graphql-documents/publisher/getPublishersByBooks.graphql");
		String expectedBookIds = jsonUtil.toJson(List.of());
		wireMock.register(
			post(urlEqualTo("/graphql"))
				.withRequestBody(matchingJsonPath("$.query", equalTo(expectedQuery)))
				.withRequestBody(matchingJsonPath("$.variables.bookIds", equalToJson(expectedBookIds, true, false)))
				.willReturn(WireMock.okJson(publishersJson))
		);

		PublisherQueryResult result = publisherGraphqlClient.queryPublishers(Set.of(), requestId).join();
		assertThat(result.data()).isEmpty();
		assertThat(result.errors())
			.hasSize(1)
			.anySatisfy(error -> {
				assertThat(error.getMessage()).isEqualTo("artificial-error");
				assertThat(error.getPath()).isEqualTo("getPublishersByBooks");
			});
	}


	@Test
	public void queryPublishers_throwsException_for4XXStatus() {
		String expectedQuery = FileReaderUtil.readFile("graphql-documents/publisher/getPublishersByBooks.graphql");
		String expectedBookIds = jsonUtil.toJson(List.of());
		wireMock.register(
			post(urlEqualTo("/graphql"))
				.withRequestBody(matchingJsonPath("$.query", equalTo(expectedQuery)))
				.withRequestBody(matchingJsonPath("$.variables.bookIds", equalToJson(expectedBookIds, true, false)))
				.willReturn(WireMock.badRequest().withBody("My Bad Request"))
		);

		CompletionException result = assertThrows(CompletionException.class, () ->
			publisherGraphqlClient.queryPublishers(Set.of(), requestId).join());
		assertThat(result.getMessage()).contains("400 Bad Request from POST");
		assertThat(result.getCause().getMessage()).contains("400 Bad Request from POST");
		assertThat(result.getCause().getCause())
			.isInstanceOfSatisfying(WebClientResponseException.class, e -> {
				assertThat(e.getStatusCode().value()).isEqualTo(400);
				assertThat(e.getResponseBodyAsString()).contains("My Bad Request");
			});
	}

	@Test
	public void queryPublishers_throwsException_for5XXStatus() {
		String expectedQuery = FileReaderUtil.readFile("graphql-documents/publisher/getPublishersByBooks.graphql");
		String expectedBookIds = jsonUtil.toJson(List.of());
		wireMock.register(
			post(urlEqualTo("/graphql"))
				.withRequestBody(matchingJsonPath("$.query", equalTo(expectedQuery)))
				.withRequestBody(matchingJsonPath("$.variables.bookIds", equalToJson(expectedBookIds, true, false)))
				.willReturn(WireMock.serverError().withBody("MY INTERNAL SERVER ERROR"))
		);

		CompletionException result = assertThrows(CompletionException.class, () ->
			publisherGraphqlClient.queryPublishers(Set.of(), requestId).join());
		assertThat(result.getMessage()).contains("500 Internal Server Error from POST");
		assertThat(result.getCause().getMessage()).contains("500 Internal Server Error from POST");
		assertThat(result.getCause().getCause())
			.isInstanceOfSatisfying(WebClientResponseException.class, e -> {
				assertThat(e.getStatusCode().value()).isEqualTo(500);
				assertThat(e.getResponseBodyAsString()).contains("MY INTERNAL SERVER ERROR");
			});
	}

	@Test
	public void createPublishers_returnsTrue_forSuccessful_operation() {
		CreatePublishersGqlResponse createPublishersGqlResponse = new CreatePublishersGqlResponse(
			new CreatePublishersGqlData(true), null);
		String json = jsonUtil.toJson(createPublishersGqlResponse);

		String expectedQuery = FileReaderUtil.readFile("graphql-documents/publisher/createPublishers.graphql");
		wireMock.register(
			post(urlEqualTo("/graphql"))
				.withRequestBody(matchingJsonPath("$.query", equalTo(expectedQuery)))
				.willReturn(WireMock.okJson(json))
		);

		PublisherInput publisher1 = new PublisherInput(UUID.randomUUID(), "publisher-1");
		PublisherInput publisher2 = new PublisherInput(UUID.randomUUID(), "publisher-2");
		Set<PublisherInput> input = Set.of(publisher1, publisher2);

		CreatePublisherMutationResult result = publisherGraphqlClient.createPublishers(input, requestId).join();

		assertThat(result.errors()).isEmpty();
		assertThat(result.data()).isEqualTo(true);

		List<LoggedRequest> loggedRequests = wireMock.find(postRequestedFor(urlEqualTo("/graphql"))
			.withRequestBody(matchingJsonPath("$.query", equalTo(expectedQuery))));

		List<CreatePublishersGqlRequest> receivedBody = loggedRequests.stream().map(LoggedRequest::getBodyAsString)
			.map(s -> jsonUtil.fromJson(s, CreatePublishersGqlRequest.class))
			.toList();
		assertThat(receivedBody)
			.hasSize(1)
			.anySatisfy(value ->
				assertThat(value.variables().publishers())
					.containsExactlyInAnyOrder(publisher1, publisher2));
	}
}
