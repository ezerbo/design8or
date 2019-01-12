package com.ss.design8or.rest;

import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ss.design8or.model.Pool;
import com.ss.design8or.model.Pools;
import com.ss.design8or.repository.PoolRepository;
import com.ss.design8or.repository.UserRepository;

/**
 * @author ezerbo
 *
 */
@RestController
@RequestMapping("/pools")
public class PoolResource {
	
	private PoolRepository repository;
	
	private UserRepository userRepository;
	
	public PoolResource(PoolRepository repository, UserRepository userRepository) {
		this.repository = repository;
		this.userRepository = userRepository;
	}
	
	@GetMapping
	public ResponseEntity<Pools> pools() {
		Pageable page = PageRequest.of(0, 2);
		Pool currentPool = getCurrentPool();
		Pools pools = Pools.builder()
				.current(currentPool)
				.past(repository.findPast(page).getContent())
				.currentPoolProgress(calculateProgress(currentPool.getAssignments().size()))
				.currentPoolParticipantsCount((long) currentPool.getAssignments().size())
				.build();
		return ResponseEntity.ok(pools);
	}
	
	private Pool getCurrentPool() {
		Optional<Pool> current = repository.findCurrent();
		if(current.isPresent()) {
			return current.get();
		}
		return repository.save(new Pool());
	}	
	
	private long calculateProgress(long currentPoolParticipantsCount) {
		long totalNumberOfUsers = userRepository.count();
		return totalNumberOfUsers == 0 ? 0 : (100 * currentPoolParticipantsCount) / totalNumberOfUsers;
	}
	
}