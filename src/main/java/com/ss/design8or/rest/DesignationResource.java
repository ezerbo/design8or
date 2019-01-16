package com.ss.design8or.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ss.design8or.model.Designation;
import com.ss.design8or.model.DesignationResponse;
import com.ss.design8or.model.User;
import com.ss.design8or.repository.DesignationRepository;
import com.ss.design8or.service.DesignationService;

/**
 * @author ezerbo
 *
 */
@RestController
@RequestMapping("/designations")
public class DesignationResource {

	private DesignationService service;
	private DesignationRepository repository;

	public DesignationResource(DesignationService service, DesignationRepository repository) {
		this.service = service;
		this.repository = repository;
	}

	@PostMapping
	public ResponseEntity<?> assignManually(@RequestBody User user) {
		service.designate(user);
		return ResponseEntity.ok(user);
	}
	
	@PostMapping("/response") //TODO use /designations/{id}/response
	public ResponseEntity<Designation> processDesignationResponse(@RequestBody DesignationResponse response) {
		Designation designation = service.processDesignationResponse(response);
		return ResponseEntity.ok(designation);
	}
	
	@GetMapping("/current")
	public ResponseEntity<Designation> findCurrent() {
		return repository.findCurrent()
					.map(d -> ResponseEntity.ok(d))
					.orElse(ResponseEntity.notFound().build());
	}
}