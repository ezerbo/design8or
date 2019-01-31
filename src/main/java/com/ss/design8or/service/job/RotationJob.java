package com.ss.design8or.service.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ss.design8or.service.DesignationService;

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
		try {
			designationService.designate();
		} catch (Exception e) {
			log.error("Unable to designate lead: {}", e.getMessage());
			e.printStackTrace();
		}
	}

}
