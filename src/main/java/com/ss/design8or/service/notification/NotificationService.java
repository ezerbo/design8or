package com.ss.design8or.service.notification;

import com.ss.design8or.model.Assignment;
import com.ss.design8or.model.Designation;
import com.ss.design8or.model.Parameter;
import com.ss.design8or.model.Pool;
import com.ss.design8or.service.PoolService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author ezerbo
 *
 */
@Service
@RequiredArgsConstructor
public class NotificationService {
	
	private final EmailService mailService;

	private final PushNotificationService pushNotificationService;

	private final WebSocketService webSocketService;

	private final PoolService poolService;
	
	@Async
	public void sendDesignationEvent(Designation designation) {
		mailService.sendDesignationEvent(designation);
		webSocketService.sendDesignationEvent(designation);
	}
	
	@Async
	public void emitParametersUpdateEvent(Parameter parameter) {
		webSocketService.sendParametersUpdateEvent(parameter);
	}
	
	@Async
	public void broadcastDesignationEvents(Designation designation) {
		poolService.getCurrentPoolCandidates().forEach(candidate -> {
			mailService.sendDesignationEvent(designation, candidate); //Broadcast to all users but the one who declined
		});
		webSocketService.sendDesignationEvent(designation); //Notify via web sockets that the current designation request has been declined
	}
	
	@Async
	public void sendAssignmentEvent(Assignment assignment) {
		webSocketService.sendAssignmentEvent(assignment);
		pushNotificationService.sendAssignmentEvent(assignment.getUser());
	}
	
	@Async
	public void sendNewPoolEvent(Pool pool) {
		webSocketService.sendPoolCreationEvent(pool);
	}
	
}