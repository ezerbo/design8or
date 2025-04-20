package com.ss.design8or.rest;

import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ss.design8or.model.Designation;
import com.ss.design8or.rest.response.DesignationResponse;
import com.ss.design8or.model.User;
import com.ss.design8or.repository.DesignationRepository;
import com.ss.design8or.service.DesignationService;

/**
 * @author ezerbo
 *
 */
@RestController
@RequestMapping("/designations")
@RequiredArgsConstructor
public class DesignationResource {

	private final DesignationService service;

	private final DesignationRepository repository;

	@PostMapping
	public ResponseEntity<?> assignManually(@RequestBody User user) {
		return ResponseEntity.ok(service.designate(user));
	}
	
	@PostMapping("/response") //TODO use /designations/{id}/response
	public ResponseEntity<Designation> processDesignationResponse(@RequestBody DesignationResponse response) {
		Designation designation = service.processDesignationResponse(response);
		return ResponseEntity.ok(designation);
	}
	
	@GetMapping( "/current")
	public ResponseEntity<Designation> findCurrent() {
		return repository.findCurrent()
					.map(ResponseEntity::ok)
					.orElse(ResponseEntity.notFound().build());
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UserDTO {

		private Long id;

		private String firstName;

		private String lastName;

		private String emailAddress;
	}
}