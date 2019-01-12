package com.ss.design8or.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.time.LocalDateTime;

import javax.annotation.PostConstruct;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jose4j.lang.JoseException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ss.design8or.config.Constants;
import com.ss.design8or.config.ServiceProperties;
import com.ss.design8or.model.KeysConfig;
import com.ss.design8or.model.NotificationData;
import com.ss.design8or.model.NotificationPayload;
import com.ss.design8or.model.Pool;
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
public class NotificationService {
	
	private KeysConfig keys;
	
	private PushService pushService;
	
	private ObjectMapper objectMapper;
	
	private SimpMessagingTemplate messagingTemplate;
	
	private SubscriptionRepository subscriptionRepository;
	
	public NotificationService(SimpMessagingTemplate messagingTemplate, ObjectMapper objectMapper,
			SubscriptionRepository subscriptionRepository, ServiceProperties properties) {
		this.keys = properties.getKeys();
		this.objectMapper = objectMapper;
		this.messagingTemplate = messagingTemplate;
		this.subscriptionRepository = subscriptionRepository;
	}
	
	@PostConstruct
	public void init() throws GeneralSecurityException {
		Security.addProvider(new BouncyCastleProvider());//TODO Go over java security providers
		pushService = new PushService(keys.getPublicKey(), keys.getPrivateKey(), keys.getSubject());
	}
	
	@Async
	public void emitDesignationEvent(User lead) {
		sendDesignationEventAsPushNotification(lead); //Broadcast event
		sendDesignationEventAsWebSocketMessage(lead);
	}
	
	public void sendDesignationEventAsWebSocketMessage(User lead) {
		messagingTemplate.convertAndSend(Constants.DESIGNATION_NSCHANNEL, lead);
	}
	
	public void notifyPoolCreation(Pool pool) {
		messagingTemplate.convertAndSend(Constants.POOL_NS_CHANNEL, pool);
	}

	public void sendDesignationEventAsPushNotification(User lead) {
		subscriptionRepository.findAll().stream()
			.map(s -> toNotification(s, getDesignationEventPayload(lead)))
			.forEach(s -> sendAsync(s));
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
	
	private String getDesignationEventPayload(User lead) {
		String designationPayload = null;
		NotificationData data = NotificationData.builder()
				.dateOfArrival(LocalDateTime.now())
				.primaryKey(1l)
				.build();
		com.ss.design8or.model.Notification notification = com.ss.design8or.model.Notification.builder()
				.title("Design8or")
				.body(String.format("%s is the new lead", lead.getEmailAddress()))
				.data(data)
				.build();
		NotificationPayload payload = NotificationPayload.builder()
				.notification(notification)
				.build();
		try {
			designationPayload = objectMapper.writeValueAsString(payload);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Unable to parse event payload.");
		}
		return designationPayload;
	}
}