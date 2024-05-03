package com.harishkannarao.springboot.graphqlwebmvc.client.graphql;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.harishkannarao.springboot.graphqlwebmvc.AbstractBaseIT;
import com.harishkannarao.springboot.graphqlwebmvc.client.graphql.dto.BookWithPublishers;
import com.harishkannarao.springboot.graphqlwebmvc.model.GraphqlData;
import com.harishkannarao.springboot.graphqlwebmvc.model.GraphqlResponse;
import com.harishkannarao.springboot.graphqlwebmvc.model.Publisher;
import com.harishkannarao.springboot.graphqlwebmvc.util.JsonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.assertj.core.api.Assertions.assertThat;

public class PublisherGraphqlClientTest extends AbstractBaseIT {

	private final PublisherGraphqlClient publisherGraphqlClient;
	private final JsonUtil jsonUtil;

	@Autowired
	public PublisherGraphqlClientTest(
		PublisherGraphqlClient publisherGraphqlClient, JsonUtil jsonUtil) {
		this.publisherGraphqlClient = publisherGraphqlClient;
		this.jsonUtil = jsonUtil;
	}

	@Test
	public void queryPublishers_returnsPublishers_byBookIds() {
		String bookId1 = "book-id-1";
		String bookId2 = "book-id-2";
		Set<String> input = Set.of(bookId1, bookId2);
		Publisher publisher1 = new Publisher("pub-id-1", "pub-name-1");
		Publisher publisher2 = new Publisher("pub-id-2", "pub-name-2");
		Publisher publisher3 = new Publisher("pub-id-3", "pub-name-3");

		BookWithPublishers book1Publishers = new BookWithPublishers(bookId1, List.of(publisher1, publisher2));
		BookWithPublishers book2Publishers = new BookWithPublishers(bookId1, List.of(publisher3));
		List<BookWithPublishers> publishers = List.of(book1Publishers, book2Publishers);
		GraphqlResponse graphqlResponse = new GraphqlResponse(new GraphqlData(publishers));
		String publishersJson = jsonUtil.toJson(graphqlResponse);

		wireMock.register(
			post(urlEqualTo("/graphql"))
				.willReturn(WireMock.okJson(publishersJson))
		);

		List<BookWithPublishers> result = publisherGraphqlClient.queryPublishers(input);
		assertThat(result)
			.contains(book1Publishers)
			.contains(book2Publishers)
			.hasSize(2);
	}
}
