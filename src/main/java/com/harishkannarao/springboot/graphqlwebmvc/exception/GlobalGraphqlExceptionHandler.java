package com.harishkannarao.springboot.graphqlwebmvc.exception;

import graphql.GraphQLError;
import graphql.schema.DataFetchingEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalGraphqlExceptionHandler {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@GraphQlExceptionHandler
	public GraphQLError handle(ArtificialException ex, DataFetchingEnvironment dataFetchingEnvironment) {
		final String message = ex.getMessage() +
			" for execution id " +
			dataFetchingEnvironment.getExecutionId();
		logger.error(message, ex);
		return GraphQLError.newError()
			.errorType(ErrorType.BAD_REQUEST)
			.location(dataFetchingEnvironment.getMergedField().getSingleField().getSourceLocation())
			.path(dataFetchingEnvironment.getExecutionStepInfo().getPath())
			.message(message)
			.build();
	}

	@GraphQlExceptionHandler
	public GraphQLError handle(DuplicateKeyException ex, DataFetchingEnvironment dataFetchingEnvironment) {
		final String message = ex.getMessage() +
			" for execution id " +
			dataFetchingEnvironment.getExecutionId();
		logger.error(message, ex);
		return GraphQLError.newError()
			.errorType(ErrorType.BAD_REQUEST)
			.location(dataFetchingEnvironment.getMergedField().getSingleField().getSourceLocation())
			.path(dataFetchingEnvironment.getExecutionStepInfo().getPath())
			.message(
				"Duplicate key / entity already exists for execution id: "
				+ dataFetchingEnvironment.getExecutionId())
			.build();
	}

}
