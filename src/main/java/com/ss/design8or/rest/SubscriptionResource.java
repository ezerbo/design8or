package com.ss.design8or.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ss.design8or.model.Subscription;
import com.ss.design8or.model.SubscriptionDTO;
import com.ss.design8or.repository.SubscriptionRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ezerbo
 *
 */
@Slf4j
@RestController
@RequestMapping("/subscriptions")
public class SubscriptionResource {
	
	//TODO check out SubroutineScanner
	private SubscriptionRepository repository;

	public SubscriptionResource(SubscriptionRepository repository) {
		this.repository = repository;
	}
	
	@PostMapping
	public ResponseEntity<Subscription> create(@RequestBody SubscriptionDTO subscriptionDTO) {
		log.info("Subscribing : {}", subscriptionDTO);
		return ResponseEntity.ok(repository.save(subscriptionDTO.toSubscription()));
	}
	
	@GetMapping
	public ResponseEntity<List<Subscription>> getAll() {
		return ResponseEntity.ok(repository.findAll());
	}
}