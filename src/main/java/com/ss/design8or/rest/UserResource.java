package com.ss.design8or.rest;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ss.design8or.model.User;
import com.ss.design8or.repository.UserRepository;

/**
 * @author ezerbo
 *
 */
@RestController
@RequestMapping("/users")
public class UserResource {

	private UserRepository repository;
	
	public UserResource(UserRepository repository) {
		this.repository = repository;
	}
	
	@GetMapping("/candidates")
	public ResponseEntity<List<User>> getNextCandidates() {
		return ResponseEntity.ok(repository.getAssignmentCandidates());
	}
	
	@GetMapping("/lead")
	public ResponseEntity<User> getLead() {
		return repository.findByLeadTrue()
				.map(lead -> ResponseEntity.ok(lead))
				.orElse(ResponseEntity.notFound().build());
	}
	
	@PostMapping
	public ResponseEntity<?> create(@RequestBody User user) {
		if(Objects.nonNull(user.getId())) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		repository.findByEmailAddress(user.getEmailAddress())
			.ifPresent(e -> {
				throw new RuntimeException(String.format("Email address '%s' is already in use", e.getEmailAddress()));
			});
		user = repository.save(user);
		return ResponseEntity.ok(user);
	}
	
	@PutMapping
	public ResponseEntity<?> update(@RequestBody User user) {
		if(Objects.isNull(user.getId())) {
			return create(user);
		}
		Optional<User> existingUser = repository.findByEmailAddress(user.getEmailAddress());
		if(existingUser.isPresent() && (user.getId() != existingUser.get().getId())) {
			throw new RuntimeException(String.format("Email address '%s' is already in use", user.getEmailAddress()));
		}
		user.setAssignments(existingUser.get().getAssignments()); // <--- done so that 'user' is not considered detached
		user = repository.save(user);
		return ResponseEntity.ok(user);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		User user = repository.findById(id)
		.orElseThrow(() -> new RuntimeException(""));
		repository.delete(user);
		return ResponseEntity.ok(null);
	}
	
}