package com.harishkannarao.springboot.graphqlwebmvc.exception;

import com.harishkannarao.springboot.graphqlwebmvc.util.Constants;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = RestController.class)
public class GlobalRestExceptionHandler {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@ExceptionHandler
	public ErrorResponse handleArtificialException(ArtificialException ex, HttpServletRequest request) {
		logger.error(ex.getMessage(), ex);
		final var detail = "%s for request id %s"
			.formatted(ex.getMessage(), request.getAttribute(Constants.X_REQUEST_ID));
		return ErrorResponse.builder(ex, HttpStatus.BAD_REQUEST, detail).build();
	}
}
