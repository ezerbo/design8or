package com.ss.design8or.service;

import org.quartz.SchedulerException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.ss.design8or.model.Parameter;
import com.ss.design8or.repository.ParameterRepository;

import lombok.extern.slf4j.Slf4j;
import static com.ss.design8or.service.Design8orUtil.formatRotationTime;

/**
 * @author ezerbo
 *
 */
@Slf4j
@Component
public class StartUpListener implements ApplicationListener<ApplicationReadyEvent> {

	private RotationService rotationService;
	
	private ParameterRepository parameterRepository;
	
	public StartUpListener(RotationService rotationService,
			ParameterRepository parameterRepository) {
		this.parameterRepository = parameterRepository;
		this.rotationService = rotationService;
	}
	
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		Parameter parameter = parameterRepository.findAll().get(0);
		try {
			rotationService.scheduleRotation(parameter.getRotationTime());
			log.info("Rotation scheduled for : '{}'", formatRotationTime(parameter.getRotationTime()));
		} catch (SchedulerException e) {
			log.error("Unable to schedule rotation time: {}", e.getMessage());
		}
		
	}

}