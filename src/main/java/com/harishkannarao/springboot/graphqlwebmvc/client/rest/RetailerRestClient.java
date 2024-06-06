package com.harishkannarao.springboot.graphqlwebmvc.client.rest;

import com.harishkannarao.springboot.graphqlwebmvc.client.rest.dto.BookWithRetailers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Set;

import static java.util.Objects.requireNonNull;

@Component
public class RetailerRestClient {
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	private final RestClient restClient;

	public RetailerRestClient(
		RestClient restClient,
		@Value("${third-party.retailer-service.rest-url}") String url) {
		this.restClient = restClient.mutate().baseUrl(url).build();
	}

	public List<BookWithRetailers> lookupRetailers(Set<String> bookIds) {
		ResponseEntity<BookWithRetailers[]> response = restClient
			.post()
			.body(bookIds)
			.retrieve()
			.toEntity(BookWithRetailers[].class);
		return List.of(requireNonNull(response.getBody()));
	}
}
