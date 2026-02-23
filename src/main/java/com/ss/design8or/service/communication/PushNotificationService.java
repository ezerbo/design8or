package com.ss.design8or.service.communication;

import com.ss.design8or.controller.request.SubscriptionRequest;
import com.ss.design8or.error.exception.ResourceInUseException;
import com.ss.design8or.error.exception.ResourceNotFoundException;
import com.ss.design8or.error.exception.ServiceException;
import com.ss.design8or.model.Subscription;
import com.ss.design8or.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.apache.http.HttpResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ezerbo
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PushNotificationService {

    private final PushService pushService;

	private final SubscriptionRepository subscriptionRepository;

	public Subscription createSubscription(SubscriptionRequest request) {
		log.debug("Creating subscription: {}", request);
		return (Subscription) subscriptionRepository.findByEndpoint(request.getEndpoint())
				.map(s -> {
					log.error("Subscription already exists");
					throw new ResourceInUseException("Subscription already exists");
				})
				.orElseGet(() -> {
					log.debug("Subscription does not exist, creating a new one");
					return subscriptionRepository.save(request.toSubscription());
				});
	}

	public Page<Subscription> getSubscriptions(Pageable pageable) {
		return subscriptionRepository.findAll(pageable);
	}
	
	@Async
	public void sendAssignmentNotification(String emailAddress) {
		log.info("Sending assignment notification for: {}", emailAddress);
		String payload = buildNotificationPayload(
			"New Lead Assigned",
			String.format("%s is now the current lead", emailAddress),
			"/pools"
		);

		List<Subscription> subscriptions = subscriptionRepository.findAll();
		log.info("Found {} subscriptions to notify", subscriptions.size());

		subscriptions.forEach(subscription -> {
			try {
				Notification notification = createPushNotification(subscription, payload);
				sendSync(notification);
				log.info("Successfully sent notification to subscription {}", subscription.getId());
			} catch (Exception e) {
				log.error("Failed to send notification to subscription {}: {}",
					subscription.getId(), e.getMessage(), e);
			}
		});
	}

	@Async
	public void sendDeclineNotification(String emailAddress) {
		log.info("Sending decline notification for: {}", emailAddress);
		String payload = buildNotificationPayload(
			"Designation Declined",
			String.format("%s declined the designation request", emailAddress),
			"/pools"
		);

		List<Subscription> subscriptions = subscriptionRepository.findAll();
		log.info("Found {} subscriptions to notify", subscriptions.size());

		subscriptions.forEach(subscription -> {
			try {
				Notification notification = createPushNotification(subscription, payload);
				sendSync(notification);
				log.info("Successfully sent notification to subscription {}", subscription.getId());
			} catch (Exception e) {
				log.error("Failed to send notification to subscription {}: {}",
					subscription.getId(), e.getMessage(), e);
			}
		});
	}

	private String buildNotificationPayload(String title, String body, String url) {
		return String.format(
			"{\"title\":\"%s\",\"body\":\"%s\",\"url\":\"%s\",\"icon\":\"/favicon.ico\"}",
			title, body, url
		);
	}

	private void sendAsync(Notification notification) {
		try {
			pushService.sendAsync(notification);
		} catch (Exception e) {
			log.error("Unable to send push notification {}", notification, e);
			throw new ServiceException(
					String.format("Unable to send push notification: %s. Error: %s", notification, e.getMessage()));
		}
	}

	private void sendSync(Notification notification) {
		try {
			HttpResponse response = pushService.send(notification);
			log.debug("Push notification sent with status: {}", response.getStatusLine());
		} catch (Exception e) {
			log.error("Unable to send push notification: {}", e.getMessage(), e);
			throw new ServiceException(
					String.format("Unable to send push notification. Error: %s", e.getMessage()));
		}
	}

	private Notification createPushNotification(Subscription s, String payload) {
		log.debug("Sending push notification: {}", s);
		try {
			return new Notification(s.getEndpoint(), s.getP256dh(), s.getAuth(), payload);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void sendNotification(long subscriptionId, String payload) {
		log.debug("Sending push notification. Subscription id: {}", subscriptionId);
		Subscription s = subscriptionRepository.findById(subscriptionId)
				.orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));
		try {
			HttpResponse httpResponse = pushService.send(new Notification(s.getEndpoint(),
					s.getP256dh(), s.getAuth(), payload));
			log.info("Push notification sent: {}", httpResponse.getStatusLine());
		} catch (Exception e) {
			log.error("Unable to send push notification {}", s, e);
			throw new ServiceException(
					String.format("Unable to send push notification: %s. Error: %s", s, e.getMessage()));
		}
	}
}