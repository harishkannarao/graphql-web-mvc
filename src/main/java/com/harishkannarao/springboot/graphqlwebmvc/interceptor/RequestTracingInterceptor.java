package com.harishkannarao.springboot.graphqlwebmvc.interceptor;

import com.harishkannarao.springboot.graphqlwebmvc.util.Constants;
import org.jetbrains.annotations.NotNull;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import reactor.core.publisher.Mono;

import java.util.Collections;

public class RequestTracingInterceptor implements WebGraphQlInterceptor {
	@Override
	public @NotNull Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
		String value = String.valueOf(request.getAttributes().get(Constants.X_REQUEST_ID));
		request.configureExecutionInput((executionInput, builder) ->
			builder.graphQLContext(Collections.singletonMap(Constants.X_REQUEST_ID, value)).build());
		return chain.next(request);
	}
}
