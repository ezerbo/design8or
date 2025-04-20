package com.ss.design8or.service.job;

import com.ss.design8or.model.Designation;
import com.ss.design8or.repository.DesignationRepository;
import com.ss.design8or.repository.UserRepository;
import com.ss.design8or.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.TimerTask;

/**
 * Broadcast events to all candidates when there is a stale designation request.
 * 
 * @author ezerbo
 *
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StaleRequestHandlerTask extends TimerTask {
	
	private final UserRepository userRepository;
	
	private final NotificationService notificationService;
	
	private final DesignationRepository designationRepository;
	
	@Override
	public void run() {
		log.info("Checking for stale requests");
        Designation designation = designationRepository.findCurrent()
				.map(Designation::stale) //Mark designation as stale
				.map(designationRepository::save)
				.orElseThrow(() -> new RuntimeException("No current designation found"));
		log.info("{}", designation);
		notificationService.emitDesignationEvent(designation, userRepository.getCurrentPoolCandidates());
	}

}