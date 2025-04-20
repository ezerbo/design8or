package com.ss.design8or.rest;

import com.ss.design8or.model.Parameter;
import com.ss.design8or.service.ParameterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author ezerbo
 *
 */
@RestController
@RequestMapping("/parameters")
@RequiredArgsConstructor
public class ParameterResource {

	private final ParameterService service;

	@GetMapping
	public ResponseEntity<?> get() {
		return ResponseEntity.ok(service.getParameter());
	}

	@PutMapping
	public ResponseEntity<?> update(@RequestBody Parameter parameter) {
		return ResponseEntity.ok(service.save(parameter));
	}
	
}