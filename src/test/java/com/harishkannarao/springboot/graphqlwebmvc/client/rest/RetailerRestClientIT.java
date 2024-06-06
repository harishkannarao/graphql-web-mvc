package com.harishkannarao.springboot.graphqlwebmvc.client.rest;

import com.harishkannarao.springboot.graphqlwebmvc.AbstractBaseIT;
import com.harishkannarao.springboot.graphqlwebmvc.client.rest.dto.BookWithRetailers;
import com.harishkannarao.springboot.graphqlwebmvc.model.Retailer;
import com.harishkannarao.springboot.graphqlwebmvc.util.JsonUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

public class RetailerRestClientIT extends AbstractBaseIT {

	private final RetailerRestClient retailerRestClient;
	private final JsonUtil jsonUtil;

	@Autowired
	public RetailerRestClientIT(
		RetailerRestClient retailerRestClient,
		JsonUtil jsonUtil) {
		this.retailerRestClient = retailerRestClient;
		this.jsonUtil = jsonUtil;
	}

	@Test
	public void lookupRetailers_returnsRetailers_forBookIds() {
		String bookId1 = "book-1";
		String bookId2 = "book-2";

		Retailer retailer1 = new Retailer(UUID.randomUUID(), "retailer-1");
		Retailer retailer2 = new Retailer(UUID.randomUUID(), "retailer-2");
		Retailer retailer3 = new Retailer(UUID.randomUUID(), "retailer-3");

		BookWithRetailers book1Retailers = new BookWithRetailers(bookId1, List.of(retailer1, retailer2));
		BookWithRetailers book2Retailers = new BookWithRetailers(bookId2, List.of(retailer2, retailer3));

		Set<String> input = Set.of(bookId1, bookId2);

		wireMock.register(
			post(urlEqualTo("/rest"))
				.withRequestBody(equalToJson(jsonUtil.toJson(input), true, false))
				.willReturn(okJson(jsonUtil.toJson(List.of(book1Retailers, book2Retailers))))
		);

		List<BookWithRetailers> result = retailerRestClient.lookupRetailers(input);

		assertThat(result)
			.anySatisfy(bookWithRetailers -> {
				assertThat(bookWithRetailers.bookId()).isEqualTo(bookId1);
				assertThat(bookWithRetailers.retailers()).containsExactlyInAnyOrder(retailer1, retailer2);
			})
			.anySatisfy(bookWithRetailers -> {
				assertThat(bookWithRetailers.bookId()).isEqualTo(bookId2);
				assertThat(bookWithRetailers.retailers()).containsExactlyInAnyOrder(retailer2, retailer3);
			})
			.hasSize(2);
	}
}
