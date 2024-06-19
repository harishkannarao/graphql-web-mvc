package com.harishkannarao.springboot.graphqlwebmvc.graphql;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import com.harishkannarao.springboot.graphqlwebmvc.AbstractBaseIT;
import com.harishkannarao.springboot.graphqlwebmvc.model.CreatePublishersResponse;
import com.harishkannarao.springboot.graphqlwebmvc.model.PublisherInput;
import com.harishkannarao.springboot.graphqlwebmvc.model.publisher.CreatePublishersGqlData;
import com.harishkannarao.springboot.graphqlwebmvc.model.publisher.CreatePublishersGqlRequest;
import com.harishkannarao.springboot.graphqlwebmvc.model.publisher.CreatePublishersGqlResponse;
import com.harishkannarao.springboot.graphqlwebmvc.util.FileReaderUtil;
import com.harishkannarao.springboot.graphqlwebmvc.util.JsonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

public class PublisherMutationIT extends AbstractBaseIT {

	private static final String BEARER_ADMIN_TOKEN = "Bearer " + "admin-token";
	private static final String BEARER_USER_TOKEN = "Bearer " + "user-token";
	private static final String BEARER_INVALID_TOKEN = "Bearer " + "invalid-token";

	private final HttpGraphQlTester httpGraphQlTester;
	private final JsonUtil jsonUtil;

	@Autowired
	public PublisherMutationIT(HttpGraphQlTester httpGraphQlTester, JsonUtil jsonUtil) {
		this.httpGraphQlTester = httpGraphQlTester;
		this.jsonUtil = jsonUtil;
	}

	@Test
	public void create_publishers_successfully() {
		PublisherInput publisher1 = new PublisherInput(UUID.randomUUID(), "publisher-name-1");
		PublisherInput publisher2 = new PublisherInput(UUID.randomUUID(), "publisher-name-2");
		Set<PublisherInput> publishers = Set.of(publisher1, publisher2);

		CreatePublishersGqlResponse createPublishersGqlResponse = new CreatePublishersGqlResponse(
			new CreatePublishersGqlData(true), null);
		String json = jsonUtil.toJson(createPublishersGqlResponse);

		String expectedQuery = FileReaderUtil
			.readFile("graphql-documents/publisher/createPublishers.graphql");
		wireMock.register(
			post(urlEqualTo("/graphql"))
				.withRequestBody(matchingJsonPath("$.query", equalTo(expectedQuery)))
				.willReturn(WireMock.okJson(json))
		);

		GraphQlTester.Response response = httpGraphQlTester
			.mutate()
			.header(HttpHeaders.AUTHORIZATION, BEARER_ADMIN_TOKEN)
			.build()
			.documentName("mutation/createPublishers")
			.variable("publishers", publishers)
			.execute();

		response.errors().satisfy(errors -> assertThat(errors).isEmpty());

		CreatePublishersResponse createPublishersResponse = response
			.path("createPublishers")
			.hasValue()
			.entity(CreatePublishersResponse.class)
			.get();

		assertThat(createPublishersResponse.success()).isTrue();
		assertThat(createPublishersResponse.message()).isEqualTo("success");

		List<LoggedRequest> loggedRequests = wireMock
			.find(postRequestedFor(urlEqualTo("/graphql"))
			.withRequestBody(matchingJsonPath("$.query", equalTo(expectedQuery))));

		List<CreatePublishersGqlRequest> receivedBody = loggedRequests.stream()
			.map(LoggedRequest::getBodyAsString)
			.map(s -> jsonUtil.fromJson(s, CreatePublishersGqlRequest.class))
			.toList();
		assertThat(receivedBody)
			.hasSize(1)
			.anySatisfy(value ->
				assertThat(value.variables().publishers())
					.containsExactlyInAnyOrder(publisher1, publisher2));
	}

	@Test
	public void create_publishers_returns_failure_on_error_from_remote_server() {
		PublisherInput publisher1 = new PublisherInput(UUID.randomUUID(), "publisher-name-1");
		PublisherInput publisher2 = new PublisherInput(UUID.randomUUID(), "publisher-name-2");
		Set<PublisherInput> publishers = Set.of(publisher1, publisher2);

		CreatePublishersGqlResponse createPublishersGqlResponse = new CreatePublishersGqlResponse(
			new CreatePublishersGqlData(false), null);
		String json = jsonUtil.toJson(createPublishersGqlResponse);

		String expectedQuery = FileReaderUtil
			.readFile("graphql-documents/publisher/createPublishers.graphql");
		wireMock.register(
			post(urlEqualTo("/graphql"))
				.withRequestBody(matchingJsonPath("$.query", equalTo(expectedQuery)))
				.willReturn(WireMock.okJson(json))
		);

		GraphQlTester.Response response = httpGraphQlTester
			.mutate()
			.header(HttpHeaders.AUTHORIZATION, BEARER_ADMIN_TOKEN)
			.build()
			.documentName("mutation/createPublishers")
			.variable("publishers", publishers)
			.execute();

		response.errors().satisfy(errors -> assertThat(errors).isEmpty());

		CreatePublishersResponse createPublishersResponse = response
			.path("createPublishers")
			.hasValue()
			.entity(CreatePublishersResponse.class)
			.get();

		assertThat(createPublishersResponse.success()).isFalse();
		assertThat(createPublishersResponse.message()).isEqualTo("failure");
	}

	@Test
	public void create_publishers_returns_error_on_remote_service_failure() {
		PublisherInput publisher1 = new PublisherInput(UUID.randomUUID(), "publisher-name-1");
		PublisherInput publisher2 = new PublisherInput(UUID.randomUUID(), "publisher-name-2");
		Set<PublisherInput> publishers = Set.of(publisher1, publisher2);

		String expectedQuery = FileReaderUtil
			.readFile("graphql-documents/publisher/createPublishers.graphql");

		wireMock.register(
			post(urlEqualTo("/graphql"))
				.withRequestBody(matchingJsonPath("$.query", equalTo(expectedQuery)))
				.willReturn(WireMock.serverError().withBody("MY INTERNAL SERVER ERROR"))
		);

		GraphQlTester.Response response = httpGraphQlTester
			.mutate()
			.header(HttpHeaders.AUTHORIZATION, BEARER_ADMIN_TOKEN)
			.build()
			.documentName("mutation/createPublishers")
			.variable("publishers", publishers)
			.execute();

		response.errors()
			.satisfy(errors -> assertThat(errors)
				.anySatisfy(error -> {
					assertThat(error.getMessage())
						.contains("INTERNAL_ERROR");
					assertThat(error.getPath()).isEqualTo("createPublishers");
				}));

		response
			.path("createPublishers")
			.valueIsNull();
	}

	@Test
	public void create_publishers_returns_forbidden_for_non_admin_users() {
		PublisherInput publisher1 = new PublisherInput(UUID.randomUUID(), "publisher-name-1");
		PublisherInput publisher2 = new PublisherInput(UUID.randomUUID(), "publisher-name-2");
		Set<PublisherInput> publishers = Set.of(publisher1, publisher2);

		GraphQlTester.Response response = httpGraphQlTester
			.mutate()
			.header(HttpHeaders.AUTHORIZATION, BEARER_USER_TOKEN)
			.build()
			.documentName("mutation/createPublishers")
			.variable("publishers", publishers)
			.execute();

		response
			.errors()
			.satisfy(errors -> assertThat(errors)
				.anySatisfy(error -> {
					assertThat(error.getMessage()).contains("Forbidden");
					assertThat(error.getPath()).isEqualTo("createPublishers");
				}))
			.path("createPublishers")
			.valueIsNull();
	}


	@Test
	public void create_publishers_returns_unauthorized_for_invalid_authentication() {
		PublisherInput publisher1 = new PublisherInput(UUID.randomUUID(), "publisher-name-1");
		PublisherInput publisher2 = new PublisherInput(UUID.randomUUID(), "publisher-name-2");
		Set<PublisherInput> publishers = Set.of(publisher1, publisher2);

		GraphQlTester.Response response = httpGraphQlTester
			.mutate()
			.header(HttpHeaders.AUTHORIZATION, BEARER_INVALID_TOKEN)
			.build()
			.documentName("mutation/createPublishers")
			.variable("publishers", publishers)
			.execute();

		response
			.errors()
			.satisfy(errors -> assertThat(errors)
				.anySatisfy(error -> {
					assertThat(error.getMessage()).contains("Unauthorized");
					assertThat(error.getPath()).isEqualTo("createPublishers");
				}))
			.path("createPublishers")
			.valueIsNull();
	}

	@Test
	public void create_publishers_returns_unauthorized_for_missing_authentication() {
		PublisherInput publisher1 = new PublisherInput(UUID.randomUUID(), "publisher-name-1");
		PublisherInput publisher2 = new PublisherInput(UUID.randomUUID(), "publisher-name-2");
		Set<PublisherInput> publishers = Set.of(publisher1, publisher2);

		GraphQlTester.Response response = httpGraphQlTester
			.documentName("mutation/createPublishers")
			.variable("publishers", publishers)
			.execute();

		response
			.errors()
			.satisfy(errors -> assertThat(errors)
				.anySatisfy(error -> {
					assertThat(error.getMessage()).contains("Unauthorized");
					assertThat(error.getPath()).isEqualTo("createPublishers");
				}))
			.path("createPublishers")
			.valueIsNull();
	}
}
