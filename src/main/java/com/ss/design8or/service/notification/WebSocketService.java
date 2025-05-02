package com.ss.design8or.service.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ss.design8or.config.WebSocketEndpoints;
import com.ss.design8or.model.Assignment;
import com.ss.design8or.model.Designation;
import com.ss.design8or.model.Parameter;
import com.ss.design8or.model.Pool;

/**
 * @author ezerbo
 *
 */
@Service
@RequiredArgsConstructor
public class WebSocketService {
	
	private final SimpMessagingTemplate messagingTemplate;
	
	@Async
	public void sendAssignmentEvent(Assignment assignment) {
		messagingTemplate.convertAndSend(WebSocketEndpoints.ASSIGNMENTS_CHANNEL, assignment);
	}
	
	@Async
	public void sendDesignationEvent(Designation designation) {
		messagingTemplate.convertAndSend(WebSocketEndpoints.DESIGNATIONS_CHANNEL, designation);
	}
	
	@Async
	public void sendParametersUpdateEvent(Parameter parameter) {
		messagingTemplate.convertAndSend(WebSocketEndpoints.PARAMETERS_CHANNEL, parameter);
	}
	
	@Async
	public void sendPoolCreationEvent(Pool pool) {
		messagingTemplate.convertAndSend(WebSocketEndpoints.POOLS_CHANNEL, pool);
	}
	
}
