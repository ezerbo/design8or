package com.ss.design8or.rest;

import java.util.NoSuchElementException;
import java.util.Objects;

import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ss.design8or.model.Parameter;
import com.ss.design8or.repository.ParameterRepository;
import com.ss.design8or.service.RotationService;
import com.ss.design8or.service.notification.NotificationService;

/**
 * @author ezerbo
 *
 */
@RestController
@RequestMapping("/parameters")
public class ParameterResource {

	private ParameterRepository repository;
	private RotationService rotationService;
	private NotificationService notificationService;

	public ParameterResource(ParameterRepository repository,
			RotationService rotationService, NotificationService notificationService) {
		this.repository = repository;
		this.rotationService = rotationService;
		this.notificationService = notificationService;
	}

	@GetMapping
	public ResponseEntity<?> get() {
		Parameter parameter = null;
		try {
			parameter = repository.findAll().stream().findFirst().get();
		} catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(parameter);
	}

	@PutMapping
	public ResponseEntity<?> update(@RequestBody Parameter parameter) throws SchedulerException {
		if(Objects.isNull(parameter.getId())) {
			throw new RuntimeException("No parameter identifier found");
		}
		parameter = repository.save(parameter);
		rotationService.rescheduleRotation(parameter.getRotationTime());
		notificationService.emitParametersUpdateEvent(parameter);
		return ResponseEntity.ok(parameter);
	}
	
	
}