package com.ss.design8or.service;

import com.ss.design8or.error.exception.ParametersNotFoundException;
import com.ss.design8or.model.Parameter;
import com.ss.design8or.repository.ParameterRepository;
import com.ss.design8or.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
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

	private final NotificationService notificationService;

	public Parameter getParameter() {
		return repository.findAll()
				.stream()
				.findFirst()
				.orElseThrow(ParametersNotFoundException::new);
	}
	
	public Parameter save(Parameter parameter) {
		parameter.setId(1L);
		rotationService.reschedule(parameter.getRotationTime());
		notificationService.emitParametersUpdateEvent(parameter);
		return repository.save(parameter);
	}
}
