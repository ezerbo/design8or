package com.ss.design8or.controller;

import com.ss.design8or.controller.pagination.PaginationParams;
import com.ss.design8or.controller.pagination.PaginationUtils;
import com.ss.design8or.controller.request.UserRequest;
import com.ss.design8or.controller.response.DesignationResponse;
import com.ss.design8or.model.User;
import com.ss.design8or.service.DesignationService;
import com.ss.design8or.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
public class UserController {

	private final UserService service;

	private final DesignationService designationService;

	@GetMapping
	public ResponseEntity<List<User>> getAll(@RequestParam(defaultValue = PaginationParams.DEFAULT_PAGE_SIZE) int size,
											 @RequestParam(defaultValue = PaginationParams.DEFAULT_PAGE_NUMBER) int page) {
		Page<User> usersPage = service.findAll(PageRequest.of(page, size));
		return ResponseEntity.ok()
				.headers(PaginationUtils.getPaginationHeaders(usersPage))
				.body(usersPage.getContent());
	}
	
	@PostMapping
	public ResponseEntity<User> create(@RequestBody UserRequest userRequest) {
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
				.orElse(ResponseEntity.notFound().build());
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.ok(null);
	}

	@PostMapping("/{id}/designations")
	public ResponseEntity<DesignationResponse> designate(@PathVariable Long id) {
		return ResponseEntity.ok(designationService.designate(id));
	}

	@PostMapping("/all/designations")
	public ResponseEntity<DesignationResponse> designate() {
		return ResponseEntity.ok(designationService.designate());
	}

	@GetMapping("/designated")
	public ResponseEntity<User> getDesignatedUser() {
		return service.designatedUser()
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}
	
}