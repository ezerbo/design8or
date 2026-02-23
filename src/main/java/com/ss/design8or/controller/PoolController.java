package com.ss.design8or.controller;

import com.ss.design8or.controller.pagination.PaginationParams;
import com.ss.design8or.controller.pagination.PaginationUtils;
import com.ss.design8or.model.Assignment;
import com.ss.design8or.model.Pool;
import com.ss.design8or.model.User;
import com.ss.design8or.service.PoolService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ezerbo
 *
 */
@RestController
@RequestMapping("/pools")
@RequiredArgsConstructor
@Tag(name = "Pools", description = "Endpoints for managing pools")
public class PoolController {

	private final PoolService poolService;

	@GetMapping
	public ResponseEntity<List<Pool>> findAll(
			@RequestParam(defaultValue = PaginationParams.DEFAULT_PAGE_SIZE) int size,
			@RequestParam(defaultValue = PaginationParams.DEFAULT_PAGE_NUMBER) int page) {
		Page<Pool> poolsPage = poolService.findAll(PageRequest.of(page, size));
		return ResponseEntity.ok()
				.headers(PaginationUtils.getPaginationHeaders(poolsPage))
				.body(poolsPage.getContent());
	}

	@GetMapping("/{id}/lead")
	public ResponseEntity<User> getCurrentLead(@PathVariable Long id) {
		return poolService.getLead(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/{id}/participants")
	public ResponseEntity<List<User>> getParticipants(
			@PathVariable Long id,
			@RequestParam(defaultValue = PaginationParams.DEFAULT_PAGE_SIZE) int size,
			@RequestParam(defaultValue = PaginationParams.DEFAULT_PAGE_NUMBER) int page) {
		Page<User> participantsPage = poolService.getParticipants(id, PageRequest.of(page, size));
		return ResponseEntity.ok()
				.headers(PaginationUtils.getPaginationHeaders(participantsPage))
				.body(participantsPage.getContent());
	}

	@GetMapping("/{id}/candidates")
	public ResponseEntity<List<User>> getCandidates(
			@PathVariable Long id,
			@RequestParam(defaultValue = PaginationParams.DEFAULT_PAGE_SIZE) int size,
			@RequestParam(defaultValue = PaginationParams.DEFAULT_PAGE_NUMBER) int page) {
		Page<User> candidatesPage = poolService.getCandidates(id, PageRequest.of(page, size));
		return ResponseEntity.ok()
				.headers(PaginationUtils.getPaginationHeaders(candidatesPage))
				.body(candidatesPage.getContent());
	}

	@GetMapping("/{id}/assignments")
	public ResponseEntity<List<Assignment>> getAssignments(@PathVariable Long id) {
		List<Assignment> assignments = poolService.getAcceptedAssignments(id);
		return ResponseEntity.ok(assignments);
	}

	@PostMapping("/start-new")
	public ResponseEntity<Pool> startNewPool() {
		Pool newPool = poolService.startNewPool();
		return ResponseEntity.ok(newPool);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletePool(@PathVariable Long id) {
		poolService.deletePool(id);
		return ResponseEntity.noContent().build();
	}

}