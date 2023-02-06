package com.ss.design8or.rest;

import com.ss.design8or.model.User;
import com.ss.design8or.repository.DesignationRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ss.design8or.model.Pool;
import com.ss.design8or.model.Pools;
import com.ss.design8or.repository.PoolRepository;
import com.ss.design8or.repository.UserRepository;

import java.util.List;

/**
 * @author ezerbo
 *
 */
@RestController
@RequestMapping("/pools")
public class PoolResource {
	
	private final PoolRepository repository;
	
	private final UserRepository userRepository;

	private final DesignationRepository designationRepository;
	
	public PoolResource(PoolRepository repository, UserRepository userRepository,
						DesignationRepository designationRepository) {
		this.repository = repository;
		this.userRepository = userRepository;
		this.designationRepository = designationRepository;
	}
	
	@GetMapping
	public ResponseEntity<Pools> pools() {
		Pool currentPool = getCurrentPool();
		long currentPoolAssignmentsCount = currentPool.getAssignments().size();
		Pools pools = Pools.builder()
				.current(currentPool)
				.past(repository.findPast(PageRequest.of(0, 2)).getContent())
				.currentPoolProgress(calculateProgress(currentPoolAssignmentsCount))
				.currentPoolParticipantsCount(currentPoolAssignmentsCount)
				.build();
		return ResponseEntity.ok(pools);
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
				.orElseGet(() -> repository.save(new Pool()));
	}
	
	private long calculateProgress(long currentPoolParticipantsCount) {
		long totalNumberOfUsers = userRepository.count();
		return totalNumberOfUsers == 0 ? 0 : (100 * currentPoolParticipantsCount) / totalNumberOfUsers;
	}
	
}