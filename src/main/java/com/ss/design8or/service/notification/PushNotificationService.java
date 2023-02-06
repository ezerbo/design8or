package com.ss.design8or.service.notification;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.time.LocalDateTime;

import javax.annotation.PostConstruct;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jose4j.lang.JoseException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ss.design8or.config.ServiceProperties;
import com.ss.design8or.model.KeysConfig;
import com.ss.design8or.model.NotificationData;
import com.ss.design8or.model.NotificationPayload;
import com.ss.design8or.model.Subscription;
import com.ss.design8or.model.User;
import com.ss.design8or.repository.SubscriptionRepository;

import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;

/**
 * @author ezerbo
 *
 */
@Slf4j
@Service
class PushNotificationService {
	
	private final KeysConfig keys;
	private PushService pushService;
	private final ObjectMapper objectMapper;
	private final SubscriptionRepository subscriptionRepository;
	
	public PushNotificationService(ServiceProperties properties, ObjectMapper objectMapper,
			SubscriptionRepository subscriptionRepository) {
		this.keys = properties.getKeys();
		this.objectMapper = objectMapper;
		this.subscriptionRepository = subscriptionRepository;
	}
	
	@PostConstruct
	public void init() throws GeneralSecurityException {
		Security.addProvider(new BouncyCastleProvider());//TODO Go over java security providers
		pushService = new PushService(keys.getPublicKey(), keys.getPrivateKey(), keys.getSubject());
	}
	
	@Async
	public void sendAssignmentEvent(User lead) {
		subscriptionRepository.findAll().stream()
			.map(s -> toNotification(s, getAssignmentEventPayload(lead)))
			.forEach(this::sendAsync);
	}

	private void sendAsync(Notification notification) {
		try {
			pushService.sendAsync(notification);
		} catch (GeneralSecurityException | IOException | JoseException e) {
			e.printStackTrace();
		}
	}

	private Notification toNotification(Subscription s, String payload) {
		log.info("Sending push notification to '{}'", s.getEndpoint());
		try {
			return new Notification(s.getEndpoint(), s.getP256dh(), s.getAuth(), payload);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private String getAssignmentEventPayload(User user) {
		NotificationPayload payload = NotificationPayload.builder()
				.notification(buildNotification(user.getEmailAddress())).build();
		try {
			return objectMapper.writeValueAsString(payload);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Unable to parse event payload.");
		}
	}
	
	private com.ss.design8or.model.Notification buildNotification(String emailAddress) {
		return com.ss.design8or.model.Notification.builder()
				.title("Design8or")
				.body(String.format("%s is the new lead", emailAddress))
				.data(getNotificationData())
				.build();
	}
	
	private NotificationData getNotificationData() {
		return NotificationData.builder()
				.dateOfArrival(LocalDateTime.now())
				.primaryKey(1L)
				.build();
	}
}