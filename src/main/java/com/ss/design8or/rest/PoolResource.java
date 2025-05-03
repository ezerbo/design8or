package com.ss.design8or.rest;

import com.ss.design8or.model.User;
import com.ss.design8or.rest.response.GetPoolsResponse;
import com.ss.design8or.service.PoolService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ezerbo
 *
 */
@RestController
@RequestMapping("/pools")
@RequiredArgsConstructor
@Tag(name = "Pools", description = "Endpoints for managing pools")
public class PoolResource {

	private final PoolService poolService;

	@GetMapping
	public ResponseEntity<GetPoolsResponse> getPools() {
		return ResponseEntity.ok(poolService.getPools());
	}

	//TODO Use /{id}/candidates instead
	@GetMapping("/current/candidates")
	public ResponseEntity<List<User>> getCandidates() {
		return ResponseEntity.ok(poolService.getCurrentPoolCandidates());
	}

	// TODO Add ability to start a new pool?
	
}