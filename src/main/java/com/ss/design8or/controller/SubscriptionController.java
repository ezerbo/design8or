package com.ss.design8or.controller;

import com.ss.design8or.controller.pagination.PaginationParams;
import com.ss.design8or.controller.pagination.PaginationUtils;
import com.ss.design8or.model.Subscription;
import com.ss.design8or.service.SubscriptionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

	private final SubscriptionService service;

	@GetMapping
	public ResponseEntity<List<Subscription>> getAll(
			@RequestParam(defaultValue = PaginationParams.DEFAULT_PAGE_SIZE) int size,
			@RequestParam(defaultValue = PaginationParams.DEFAULT_PAGE_NUMBER) int page) {
		Page<Subscription> subscriptionsPage = service.findAll(PageRequest.of(page, size));
		return ResponseEntity.ok()
				.headers(PaginationUtils.getPaginationHeaders(subscriptionsPage))
				.body(subscriptionsPage.getContent());
	}
}
