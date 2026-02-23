package com.ss.design8or.controller;

import com.ss.design8or.controller.pagination.PaginationParams;
import com.ss.design8or.controller.pagination.PaginationUtils;
import com.ss.design8or.controller.request.SubscriptionRequest;
import com.ss.design8or.model.Subscription;
import com.ss.design8or.service.communication.PushNotificationService;
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
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Subscriptions", description = "Endpoints for managing subscriptions")
public class PushNotificationController {
	
	private final PushNotificationService pushNotificationService;

	@PostMapping
	public ResponseEntity<Subscription> create(@RequestBody SubscriptionRequest request) {
		return ResponseEntity.ok(pushNotificationService.createSubscription(request));
	}
	
	@GetMapping
	public ResponseEntity<List<Subscription>> getAll(
			@RequestParam(defaultValue = PaginationParams.DEFAULT_PAGE_NUMBER) int page,
			@RequestParam(defaultValue = PaginationParams.DEFAULT_PAGE_SIZE) int size) {
		Page<Subscription> subscriptionsPage = pushNotificationService.getSubscriptions(PageRequest.of(page, size));
		return ResponseEntity.ok()
				.headers(PaginationUtils.getPaginationHeaders(subscriptionsPage))
				.body(subscriptionsPage.getContent());
	}

	@PostMapping("/subscriptions/{id}/send")
	public ResponseEntity<Void> sendNotification(@PathVariable Long id,
												 @RequestBody String payload) {
		pushNotificationService.sendNotification(id, payload);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/test")
	public ResponseEntity<String> testNotification() {
		pushNotificationService.sendAssignmentNotification("test@example.com");
		return ResponseEntity.ok("Test notification sent to all subscribers");
	}
}