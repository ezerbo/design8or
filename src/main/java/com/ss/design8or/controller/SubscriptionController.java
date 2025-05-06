package com.ss.design8or.controller;

import com.ss.design8or.error.exception.ResourceInUseException;
import com.ss.design8or.model.Subscription;
import com.ss.design8or.repository.SubscriptionRepository;
import com.ss.design8or.controller.request.SubscriptionRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author ezerbo
 *
 */
@Slf4j
@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions", description = "Endpoints for managing subscriptions")
public class SubscriptionController {
	
	private final SubscriptionRepository repository;

	@PostMapping
	public ResponseEntity<Subscription> create(@RequestBody SubscriptionRequest request) {
		log.info("Creating a subscription : {}", request);
		if (repository.existsByEndpointOrAuthOrP256dh(request.getEndpoint(),
				request.getKeys().getAuth(), request.getKeys().getP256dh())) {
			log.error("Subscription already exists");
			throw new ResourceInUseException("Subscription already exists");
		}
		return ResponseEntity.ok(repository.save(request.toSubscription()));
	}
	
	@GetMapping
	public ResponseEntity<List<Subscription>> getAll() {
		return ResponseEntity.ok(repository.findAll());
	}
}