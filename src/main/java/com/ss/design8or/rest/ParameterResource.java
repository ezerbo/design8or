package com.ss.design8or.rest;

import java.util.Objects;

import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ss.design8or.model.Parameter;
import com.ss.design8or.service.ParameterService;
import com.ss.design8or.service.RotationService;
import com.ss.design8or.service.notification.NotificationService;

/**
 * @author ezerbo
 *
 */
@RestController
@RequestMapping("/parameters")
public class ParameterResource {

	private ParameterService service;
	private RotationService rotationService;
	private NotificationService notificationService;

	public ParameterResource(ParameterService service,
			RotationService rotationService, NotificationService notificationService) {
		this.service = service;
		this.rotationService = rotationService;
		this.notificationService = notificationService;
	}

	@GetMapping
	public ResponseEntity<?> get() {
		return service.getOptionalParamter()
				.map(p -> ResponseEntity.ok(p))
				.orElse(ResponseEntity.notFound().build());
	}

	@PutMapping
	public ResponseEntity<?> update(@RequestBody Parameter parameter) throws SchedulerException {
		if(Objects.isNull(parameter.getId())) {
			throw new RuntimeException("No parameter identifier found");
		}
		parameter = service.save(parameter);
		rotationService.rescheduleRotation(parameter.getRotationTime());
		notificationService.emitParametersUpdateEvent(parameter);
		return ResponseEntity.ok(parameter);
	}
	
	
}