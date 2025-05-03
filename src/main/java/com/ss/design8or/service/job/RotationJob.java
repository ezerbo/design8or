package com.ss.design8or.service.job;

import com.ss.design8or.service.DesignationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

/**
 * @author ezerbo
 *
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RotationJob implements Job {

	private final DesignationService designationService;
	
	@Override
	public void execute(JobExecutionContext context) {
        designationService.designate();
    }
}
