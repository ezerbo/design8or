package com.ss.design8or.rest;

import com.ss.design8or.model.User;
import com.ss.design8or.rest.request.UserRequest;
import com.ss.design8or.rest.response.DesignationResponse;
import com.ss.design8or.service.DesignationService;
import com.ss.design8or.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ezerbo
 *
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints for managing users")
public class UserResource {

	private final UserService service;

	private final DesignationService designationService;

	@PostMapping("/{id}/designations")
	public ResponseEntity<DesignationResponse> designate(@PathVariable Long id) {
		return ResponseEntity.ok(designationService.designate(id));
	}

	@GetMapping
	public ResponseEntity<List<User>> getAll() {
		return ResponseEntity.ok(service.findAll());
	}
	
	@PostMapping
	public ResponseEntity<User> create(@RequestBody UserRequest userRequest) {
		log.info("Creating user {}", userRequest);
		return ResponseEntity.ok(service.create(userRequest));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<User> update(@RequestBody UserRequest userRequest, @PathVariable Long id) {
		return ResponseEntity.ok(service.update(userRequest, id));
	}

	@GetMapping("/{id}")
	public ResponseEntity<User> getOne(@PathVariable Long id) {
		return service.getOne(id)
				.map(ResponseEntity::ok)
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.ok(null);
	}
	
}