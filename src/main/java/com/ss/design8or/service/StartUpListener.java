package com.ss.design8or.service;

import com.ss.design8or.repository.ParameterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

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

	private final StaleReqService staleRequestHandler;
	
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		LocalTime rotationTime = parameterRepository.findAll()
				.getFirst()
				.getRotationTime();
        staleRequestHandler.startTimer();
        rotationService.reschedule(rotationTime);
        log.info("Rotation scheduled for : '{}'", formatRotationTime(rotationTime));
    }
}