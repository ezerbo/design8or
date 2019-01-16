package com.ss.design8or.service.notification;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ss.design8or.model.Assignment;
import com.ss.design8or.model.Designation;
import com.ss.design8or.model.Parameter;
import com.ss.design8or.model.Pool;

/**
 * @author ezerbo
 *
 */
@Service
public class NotificationService {
	
	private EmailNotificationService mailService;
	private PushNotificationService pushNotificationService;
	private WebSocketNotificationService webSocketNotificationService;
	
	public NotificationService(EmailNotificationService mailService,
			PushNotificationService pushNotificationService, WebSocketNotificationService webSocketNotificationService) {
		this.mailService = mailService;
		this.pushNotificationService = pushNotificationService;
		this.webSocketNotificationService = webSocketNotificationService;
	}
	
	@Async
	public void emitDesignationEvent(Designation designation) {
		mailService.sendDesignationEvent(designation);
		webSocketNotificationService.sendDesignationEvent(designation);
	}
	
	@Async
	public void emitParametersUpdateEvent(Parameter parameter) {
		webSocketNotificationService.sendParametersUpdateEvent(parameter);
	}
	
	@Async
	public void emitDesignationDeclinationEvent(Designation designation) {
		//sendAssignmentEventAsWebSocketMessage(assignment);
		//TODO broadcast to all user but the one who declined
	}
	
	@Async
	public void emitAssignmentEvent(Assignment assignment) {
		webSocketNotificationService.sendAssignmentEvent(assignment);
		pushNotificationService.sendAssignmentEvent(assignment.getUser());
	}
	
	@Async
	public void emitPoolCreationEvent(Pool pool) {
		webSocketNotificationService.sendPoolCreationEvent(pool);
	}
	
}