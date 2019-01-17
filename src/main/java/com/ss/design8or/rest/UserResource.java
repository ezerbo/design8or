package com.ss.design8or.rest;

import java.util.List;
import java.util.Objects;

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
import com.ss.design8or.repository.DesignationRepository;
import com.ss.design8or.service.UserService;

/**
 * @author ezerbo
 *
 */
@RestController
@RequestMapping("/users")
public class UserResource {

	private UserService service;
	private DesignationRepository designationRepository;
	
	public UserResource(UserService service,
			DesignationRepository designationRepository) {
		this.service = service;
		this.designationRepository = designationRepository;
	}
	
	@GetMapping
	public ResponseEntity<List<User>> getAll() {
		return ResponseEntity.ok(service.getAll());
	}
	
	@GetMapping("/candidates")
	public ResponseEntity<List<User>> getNextCandidates() {
		return ResponseEntity.ok(service.getCurrentPoolCandidates());
	}
	
	@GetMapping("/lead")
	public ResponseEntity<User> getLeadUser() {
		return service.getCurrentLead()
				.map(lead -> ResponseEntity.ok(lead))
				.orElse(ResponseEntity.notFound().build());
	}
	
	@GetMapping("/designated")
	public ResponseEntity<User> getDesignatedUser() {
		return designationRepository.findCurrent()
				.map(d -> ResponseEntity.ok(d.getUser()))
				.orElse(ResponseEntity.notFound().build());
	}
	
	@PostMapping
	public ResponseEntity<?> create(@RequestBody User user) {
		if(Objects.nonNull(user.getId())) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		user =	service.create(user);
		return ResponseEntity.ok(user);
	}
	
	@PutMapping
	public ResponseEntity<?> update(@RequestBody User user) {
		final long userId = user.getId();
		if(Objects.isNull(userId)) {
			return create(user);
		}
		user = service.update(user);
		return ResponseEntity.ok(user);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.ok(null);
	}
	
}