package com.ss.design8or.rest;

import com.ss.design8or.model.User;
import com.ss.design8or.rest.request.CreateUserRequest;
import com.ss.design8or.service.UserService;
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
// TODO Use response objects instead of generics
public class UserResource {

	private final UserService service;

	@GetMapping
	public ResponseEntity<List<User>> getAll() {
		return ResponseEntity.ok(service.getAll());
	}
	
	@PostMapping
	public ResponseEntity<?> create(@RequestBody CreateUserRequest request) {
		log.info("Creating user {}", request);
		return ResponseEntity.ok(service.create(request));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<?> update(@RequestBody User user, @PathVariable Long id) {
		user.setId(id);
		return ResponseEntity.ok(service.update(user));
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getOne(@PathVariable Long id) {
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