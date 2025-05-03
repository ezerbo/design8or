package com.ss.design8or.error;

import com.ss.design8or.error.exception.ResourceInUseException;
import com.ss.design8or.error.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author ezerbo
 *
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler({ Exception.class })
	public ResponseEntity<ErrorVM> runtimeException(Exception e) {
		return new ResponseEntity<>(ErrorVM.builder()
				.description("Unable to process request")
				.message(e.getMessage())
				.build(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({ ResourceInUseException.class})
	public ResponseEntity<ErrorVM> badRequests(RuntimeException e) {
		return new ResponseEntity<>(ErrorVM.builder()
						.description("Unable to process request")
						.message(e.getMessage())
						.build(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler({ ResourceNotFoundException.class })
	public ResponseEntity<ErrorVM> notFound(ResourceNotFoundException e) {
		return new ResponseEntity<>(ErrorVM.builder()
				.description("Unable to process request due to missing resource")
				.message(e.getMessage())
				.build(), HttpStatus.NOT_FOUND);
	}
}
