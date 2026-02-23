package com.ss.design8or.controller;

import com.ss.design8or.controller.pagination.PaginationParams;
import com.ss.design8or.controller.pagination.PaginationUtils;
import com.ss.design8or.controller.response.DesignationAnswer;
import com.ss.design8or.controller.response.DesignationResponse;
import com.ss.design8or.model.Assignment;
import com.ss.design8or.service.DesignationService;
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
@RequestMapping("/designations")
@RequiredArgsConstructor
@Tag(name = "Designations", description = "Endpoints for managing designations")
public class DesignationController {

	private final DesignationService service;

	@GetMapping("/{id}/response")
	public ResponseEntity<DesignationResponse> processResponse(@PathVariable long id,
															   @RequestParam DesignationAnswer answer,
															   @RequestParam String emailAddress) {
		return ResponseEntity.ok(service.processResponse(id, emailAddress, answer));
	}

	@GetMapping
	public ResponseEntity<List<Assignment>> findAll(
			@RequestParam(defaultValue = PaginationParams.DEFAULT_PAGE_NUMBER) int page,
			@RequestParam(defaultValue = PaginationParams.DEFAULT_PAGE_SIZE) int size) {
		Page<Assignment> assignmentsPage = service.findAll(PageRequest.of(page, size));
		return ResponseEntity.ok()
				.headers(PaginationUtils.getPaginationHeaders(assignmentsPage))
				.body(assignmentsPage.getContent());
	}

	@GetMapping("/current")
	public ResponseEntity<Assignment> getCurrent() {
		return service.getCurrentDesignation()
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping("/assignments/{assignmentId}/accept")
	public ResponseEntity<Assignment> acceptDesignation(@PathVariable Long assignmentId) {
		return ResponseEntity.ok(service.accept(assignmentId));
	}

	@PostMapping("/assignments/{assignmentId}/decline")
	public ResponseEntity<Assignment> declineDesignation(@PathVariable Long assignmentId) {
		return ResponseEntity.ok(service.decline(assignmentId));
	}

	@PostMapping("/assignments/{assignmentId}/designate")
	public ResponseEntity<Assignment> designateManually(@PathVariable Long assignmentId) {
		return ResponseEntity.ok(service.designateManually(assignmentId));
	}

}