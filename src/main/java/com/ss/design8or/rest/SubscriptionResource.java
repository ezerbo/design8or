package com.ss.design8or.rest;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ss.design8or.error.exception.SubscriptionException;
import com.ss.design8or.model.Subscription;
import com.ss.design8or.rest.request.CreateSubscriptionRequest;
import com.ss.design8or.repository.SubscriptionRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ezerbo
 *
 */
@Slf4j
@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionResource {
	
	private final SubscriptionRepository repository;

	@PostMapping
	public ResponseEntity<Subscription> create(@RequestBody CreateSubscriptionRequest request) {
		log.info("Creating a subscription : {}", request);
		if (repository.existsByEndpointOrAuthOrP256dh(request.getEndpoint(),
				request.getKeys().getAuth(), request.getKeys().getP256dh())) {
			log.error("Subscription already exists");
			throw new SubscriptionException();
		}
		return ResponseEntity.ok(repository.save(request.toSubscription()));
	}
	
	@GetMapping
	public ResponseEntity<List<Subscription>> getAll() {
		return ResponseEntity.ok(repository.findAll());
	}
}