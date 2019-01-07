package com.ss.design8or.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ss.design8or.model.Pool;
import com.ss.design8or.repository.PoolRepository;

/**
 * @author ezerbo
 *
 */
@RestController
@RequestMapping("/pools")
public class PoolResource {
	
	private PoolRepository repository;
	
	public PoolResource(PoolRepository repository) {
		this.repository = repository;
	}
	
	@GetMapping
	public ResponseEntity<Map<String, Object>> pools() {
		Pageable page = PageRequest.of(0, 3);
		Map<String, Object> pools =new HashMap<>();
		pools.put("current", getCurrentPool());
		pools.put("past", repository.findPast(page).getContent());
		return ResponseEntity.ok(pools);
	}
	
	private Pool getCurrentPool() {
		Optional<Pool> current = repository.findCurrent();
		if(current.isPresent()) {
			return current.get();
		}
		return repository.save(new Pool());
	}
	
}