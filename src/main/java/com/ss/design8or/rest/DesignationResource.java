package com.ss.design8or.rest;

import com.ss.design8or.model.Designation;
import com.ss.design8or.model.DesignationStatus;
import com.ss.design8or.repository.DesignationRepository;
import com.ss.design8or.rest.response.DesignationAnswer;
import com.ss.design8or.rest.response.DesignationResponse;
import com.ss.design8or.service.DesignationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author ezerbo
 *
 */
@RestController
@RequestMapping("/designations")
@RequiredArgsConstructor
@Tag(name = "Designations", description = "Endpoints for managing designations")
public class DesignationResource {

	private final DesignationService service;

	private final DesignationRepository repository;
	
	@GetMapping("/{id}/response")
	public ResponseEntity<DesignationResponse> processResponse(@PathVariable long id,
															   @RequestParam DesignationAnswer answer,
															   @RequestParam String emailAddress) {
		return ResponseEntity.ok(service.processResponse(id, emailAddress, answer));
	}

	// TODO Implement getAll and order by date
	@GetMapping( "/current")
	public ResponseEntity<Designation> findCurrent() {
		return repository.findOneByStatus(DesignationStatus.PENDING)
					.map(ResponseEntity::ok)
					.orElse(ResponseEntity.notFound().build());
	}

}