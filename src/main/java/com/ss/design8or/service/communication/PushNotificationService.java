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
		subscriptionRepository.findAll()
				.stream()
				.map(s -> createPushNotification(s, String.format("%s is the new lead", emailAddress)))
				.forEach(this::sendAsync);
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