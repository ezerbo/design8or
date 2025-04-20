package com.ss.design8or.service;

import com.ss.design8or.model.Parameter;
import com.ss.design8or.repository.ParameterRepository;
import com.ss.design8or.service.job.StaleRequestHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import static com.ss.design8or.service.Design8orUtil.formatRotationTime;

/**
 * @author ezerbo
 *
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StartUpListener implements ApplicationListener<ApplicationReadyEvent> {

	private final RotationService rotationService;
	
	private final ParameterRepository parameterRepository;

	private final StaleRequestHandler staleRequestHandler;

	
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		Parameter parameter = parameterRepository.findAll().getFirst();
        staleRequestHandler.startTimer();
        rotationService.scheduleRotation(parameter.getRotationTime());
        log.info("Rotation scheduled for : '{}'", formatRotationTime(parameter.getRotationTime()));
    }
}