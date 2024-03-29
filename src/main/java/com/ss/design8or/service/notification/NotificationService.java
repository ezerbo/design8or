package com.ss.design8or.service.notification;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ss.design8or.model.Assignment;
import com.ss.design8or.model.Designation;
import com.ss.design8or.model.Parameter;
import com.ss.design8or.model.Pool;
import com.ss.design8or.model.User;

/**
 * @author ezerbo
 *
 */
@Service
public class NotificationService {
	
	private final EmailNotificationService mailService;
	private final PushNotificationService pushNotificationService;
	private final WebSocketNotificationService webSocketNotificationService;
	
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
	public void emitDesignationEvent(Designation designation, List<User> candidates) {
		candidates
		.forEach(candidate -> {
			mailService.sendDesignationEvent(designation, candidate); //Broadcast to all users but the one who declined
		});
		webSocketNotificationService.sendDesignationEvent(designation); //Notify via web sockets that the current designation request has been declined
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