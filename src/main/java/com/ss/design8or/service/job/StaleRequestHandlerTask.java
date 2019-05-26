package com.ss.design8or.service.job;

import java.util.List;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ss.design8or.model.Designation;
import com.ss.design8or.model.User;
import com.ss.design8or.repository.DesignationRepository;
import com.ss.design8or.repository.UserRepository;
import com.ss.design8or.service.notification.NotificationService;

import lombok.extern.slf4j.Slf4j;

/**
 * Broadcast events to all candidates when there is a stale designation request.
 * 
 * @author ezerbo
 *
 */
@Slf4j
@Component
public class StaleRequestHandlerTask extends TimerTask {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private DesignationRepository designationRepository;
	
	@Override
	public void run() {
		log.info("Checking for stale requests");
		List<User> candidates = userRepository.getCurrentPoolCandidates();
		Designation designation = designationRepository.findCurrent()
				.map(d -> d.stale()) //Mark designation as stale
				.map(d -> designationRepository.save(d))
				.orElseThrow(() -> new RuntimeException("No current designation found"));
		log.info("{}", designation);
		notificationService.emitDesignationEvent(designation, candidates);
	}

}