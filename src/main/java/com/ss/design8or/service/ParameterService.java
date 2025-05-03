package com.ss.design8or.service;

import com.ss.design8or.config.WebSocketEndpoints;
import com.ss.design8or.error.exception.ResourceNotFoundException;
import com.ss.design8or.model.Parameter;
import com.ss.design8or.repository.ParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * @author ezerbo
 *
 */
@Service
@RequiredArgsConstructor
public class ParameterService {

	private final ParameterRepository repository;

	private final RotationService rotationService;

	private final SimpMessagingTemplate simpMessagingTemplate;

	public Parameter getParameter() {
		return repository.findAll()
				.stream()
				.findFirst()
				.orElseThrow(() -> new ResourceNotFoundException("No parameter found."));
	}
	
	public Parameter save(Parameter parameter) {
		parameter.setId(1L);
		rotationService.reschedule(parameter.getRotationTime());
		simpMessagingTemplate.convertAndSend(WebSocketEndpoints.PARAMETERS_CHANNEL, parameter);
		return repository.save(parameter);
	}
}
