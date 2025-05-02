package com.ss.design8or.rest;

import com.ss.design8or.model.Designation;
import com.ss.design8or.model.DesignationStatus;
import com.ss.design8or.repository.DesignationRepository;
import com.ss.design8or.rest.request.DesignationRequest;
import com.ss.design8or.rest.response.DesignationAnswer;
import com.ss.design8or.rest.response.DesignationResponse;
import com.ss.design8or.service.DesignationService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

	@PostMapping("/current/reassign")
	public ResponseEntity<DesignationResponse> reassign(@RequestBody DesignationRequest request) {
		return ResponseEntity.ok(service.designate(request.getUserId()));
	}
	
	@GetMapping("/{id}/response")
	public ResponseEntity<DesignationResponse> processResponse(@PathVariable long id,
															   @RequestParam DesignationAnswer answer) {
		return ResponseEntity.ok(service.processResponse(id, answer));
	}
	
	@GetMapping( "/current")
	public ResponseEntity<Designation> findCurrent() {
		return repository.findOneByStatus(DesignationStatus.PENDING)
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