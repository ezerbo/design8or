package com.ss.design8or.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.ss.design8or.config.Constants;
import com.ss.design8or.model.User;

/**
 * @author ezerbo
 *
 */
@Component
public class NotificationService {
	
	private SimpMessagingTemplate messagingTemplate;
	
	public NotificationService(SimpMessagingTemplate messagingTemplate) {
		this.messagingTemplate = messagingTemplate;
	}
	
	public void notifyDesignation(User lead) {
		messagingTemplate.convertAndSend(Constants.DESIGNATION_NSCHANNEL, lead);
	}
}
