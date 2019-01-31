package com.ss.design8or.service.job;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ss.design8or.model.Designation;
import com.ss.design8or.model.User;
import com.ss.design8or.repository.DesignationRepository;
import com.ss.design8or.repository.UserRepository;
import com.ss.design8or.service.notification.NotificationService;

/**
 * Broadcast events to all candidates when there is a stale designation request.
 * 
 * @author ezerbo
 *
 */

@Component
public class StaleRequestEventBroadcastJob implements Job {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private DesignationRepository designationRepository;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		List<User> candidates = userRepository.getCurrentPoolCandidates();
		Designation designation = designationRepository.findCurrent()
				.map(d -> d.stale())
				.map(d -> designationRepository.save(d))
				.orElseThrow(() -> new RuntimeException("No current designation found"));
		notificationService.emitDesignationEvent(designation, candidates);
	}

}