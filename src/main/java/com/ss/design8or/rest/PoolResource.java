package com.ss.design8or.rest;

import com.ss.design8or.model.*;
import com.ss.design8or.repository.DesignationRepository;
import com.ss.design8or.rest.response.GetPoolsResponse;
import com.ss.design8or.rest.response.PoolDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ss.design8or.repository.PoolRepository;
import com.ss.design8or.repository.UserRepository;

import java.util.List;

/**
 * @author ezerbo
 *
 */
@RestController
@RequestMapping("/pools")
@RequiredArgsConstructor
public class PoolResource {
	
	private final PoolRepository repository;
	
	private final UserRepository userRepository;

	private final DesignationRepository designationRepository;
	
	@GetMapping
	public ResponseEntity<GetPoolsResponse> pools() {
		Pool currentPool = getCurrentPool();
		long assignmentsCount = currentPool.getAssignments().size();
		GetPoolsResponse getPoolsResponse = GetPoolsResponse.builder()
				.current(currentPool)
				.past(repository.findPast(PageRequest.of(0, 2)).getContent())
				.progress(calculateProgress(assignmentsCount))
				.participantsCount(assignmentsCount)
				.build();
		return ResponseEntity.ok(getPoolsResponse);
	}

	//TODO Handle case where there's no lead for a newly created pool
	@GetMapping("/stats")
	public ResponseEntity<CurrentPoolStats> stats() {
		Pool pool = getCurrentPool();
		long assignmentsCount = pool.getAssignments().size();
		CurrentPoolStats stats = CurrentPoolStats.builder()
				.count(repository.count())
				.pool(PoolDTO.builder()
						.id(pool.getId())
						.progress(calculateProgress(assignmentsCount))
						.participantsCount(assignmentsCount)
						.startDate(pool.getStartDate())
						.endDate(pool.getEndDate())
						.lead(userRepository.findByLeadTrue()
								.map(user -> DesignationResource.UserDTO.builder()
										.firstName(user.getFirstName())
										.lastName(user.getLastName())
										.emailAddress(user.getEmailAddress())
										.build()).orElse(null))
						.build())
				.build();
		return ResponseEntity.ok(stats);
	}

	@GetMapping("/current/candidates")
	public ResponseEntity<List<User>> getNextCandidates() {
		return ResponseEntity.ok(userRepository.getCurrentPoolCandidates());
	}

	@GetMapping("/current/lead")
	public ResponseEntity<User> getLeadUser() {
		return userRepository.findByLeadTrue()
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/current/designated")
	public ResponseEntity<User> getDesignatedUser() {
		return designationRepository.findCurrent()
				.map(d -> ResponseEntity.ok(d.getUser()))
				.orElse(ResponseEntity.notFound().build());
	}
	
	private Pool getCurrentPool() {
		return repository.findCurrent()
				.orElseGet(() -> repository.save(new Pool())); // Creates one if none exists
	}
	
	private long calculateProgress(long participantsCount) {
		long usersCount = userRepository.count();
		return usersCount == 0 ? 0 : 100 * (participantsCount / usersCount);
	}
	
}