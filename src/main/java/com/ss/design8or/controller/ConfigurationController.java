package com.ss.design8or.controller;

import com.ss.design8or.model.Configuration;
import com.ss.design8or.service.ConfigurationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author ezerbo
 *
 */
@Slf4j
@RestController
@RequestMapping("/configurations")
@RequiredArgsConstructor
@Tag(name = "Configurations", description = "Endpoints for managing configurations")
public class ConfigurationController {

	private final ConfigurationService service;

	@GetMapping
	public ResponseEntity<List<Configuration>> getAll() {
		return ResponseEntity.ok(service.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Configuration> getOne(@PathVariable Long id) {
		return ResponseEntity.ok(service.findById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Configuration> update(@PathVariable Long id, @RequestBody Map<String, String> request) {
		String value = request.get("value");
		return ResponseEntity.ok(service.update(id, value));
	}

	@GetMapping("/key/{key}")
	public ResponseEntity<Configuration> getByKey(@PathVariable String key) {
		return ResponseEntity.ok(service.findByKey(key));
	}
}
