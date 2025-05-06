package com.ss.design8or.service.listener;

import com.ss.design8or.service.rotation.RotationService;
import com.ss.design8or.service.rotation.StaleReqService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author ezerbo
 *
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StartUpListener implements ApplicationListener<ApplicationReadyEvent> {

	private final RotationService rotationService;
	
	private final StaleReqService staleRequestHandler;
	
	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		staleRequestHandler.run();
		rotationService.schedule();
    }
}