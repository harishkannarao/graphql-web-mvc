package com.harishkannarao.springboot.graphqlwebmvc.client.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class RestClientAccessLoggingInterceptor implements ClientHttpRequestInterceptor {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] body, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
		long startTime = System.currentTimeMillis();
		int statusCode = 0;
		String method = httpRequest.getMethod().name();
		String url = httpRequest.getURI().toString();
		String responseBody = "";
		ClientHttpResponse response;
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Request Body: " + new String(body));
			}
			response = clientHttpRequestExecution.execute(httpRequest, body);
			statusCode = response.getStatusCode().value();
			if (logger.isDebugEnabled()) {
				responseBody = new String(response.getBody().readAllBytes());
			}
		} finally {
			long timeTaken = System.currentTimeMillis() - startTime;
			logger.info("REST_CLIENT_ACCESS_LOG {} {} {} {}", timeTaken, statusCode, method, url);
			logger.debug("Response Body: " + responseBody);
		}
		return response;
	}
}
