package com.ss.design8or.rest;

import com.ss.design8or.rest.response.CurrentPoolStats;
import com.ss.design8or.model.User;
import com.ss.design8or.rest.response.GetPoolsResponse;
import com.ss.design8or.service.PoolService;
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
public class PoolResource {

	private final PoolService poolService;
	
	@GetMapping
	public ResponseEntity<GetPoolsResponse> getPastPools() {
		return ResponseEntity.ok(poolService.getPastPools());
	}

	@GetMapping("/stats")
	public ResponseEntity<CurrentPoolStats> getCurrentPoolStats() {
		return ResponseEntity.ok(poolService.getCurrentPoolStats());
	}

	@GetMapping("/current/candidates")
	public ResponseEntity<List<User>> getCurrentPoolCandidates() {
		return ResponseEntity.ok(poolService.getCurrentPoolCandidates());
	}

	@GetMapping("/current/lead")
	public ResponseEntity<User> getCurrentPoolLead() {
		return poolService.getCurrentLead()
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}
	
}