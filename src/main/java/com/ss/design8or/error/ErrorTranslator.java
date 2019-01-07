package com.ss.design8or.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ezerbo
 *
 */
@Slf4j
@RestControllerAdvice
public class ErrorTranslator {

	@ResponseBody
	@ExceptionHandler({ Exception.class })
	public ResponseEntity<ErrorVM> processRuntimeException(Exception ex) {
		log.error("{}", ex.getMessage());
		ex.printStackTrace();
		return new ResponseEntity<>(
				ErrorVM.builder()
				.descripton("Unable to process request")
				.message(ex.getMessage())
				.build(),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
