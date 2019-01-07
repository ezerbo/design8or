package com.ss.design8or.service;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ss.design8or.model.User;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ezerbo
 *
 */
@Slf4j
@Component
public class RotationJob implements Job {

	@Autowired
	private DesignationService designationService;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.info("Designating lead...");
		try {
			User user = designationService.designate();
			log.info("Designated lead: {}", user.getEmailAddress());
		} catch (Exception e) {
			log.error("Unable to designate lead: {}", e.getMessage());
		}
	}

}
