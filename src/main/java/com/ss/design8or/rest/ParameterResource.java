package com.ss.design8or.rest;

import com.ss.design8or.model.Parameter;
import com.ss.design8or.service.ParameterService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Parameters", description = "Endpoints for managing application parameters")
public class ParameterResource {

	private final ParameterService service;

	@GetMapping
	public ResponseEntity<Parameter> get() {
		return ResponseEntity.ok(service.getParameter());
	}

	@PutMapping
	public ResponseEntity<Parameter> update(@RequestBody Parameter parameter) {
		return ResponseEntity.ok(service.save(parameter));
	}
	
}