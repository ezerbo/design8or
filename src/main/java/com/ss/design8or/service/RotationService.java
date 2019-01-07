package com.ss.design8or.service;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.time.LocalTime;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Component;

/**
 * @author ezerbo
 *
 */
@Component
public class RotationService {

	private Scheduler jobScheduler;
	
	public RotationService(Scheduler jobScheduler) {
		this.jobScheduler = jobScheduler;
	}
	
	public void scheduleRotation(LocalTime rotationTime) throws SchedulerException {
		Trigger trigger = createTrigger(rotationTime);
		JobDetail jobDetail = newJob(RotationJob.class)
				.withIdentity("rotationJob", "design8orGroup")
				.build();
		jobScheduler.scheduleJob(jobDetail, trigger);
	}
	
	public void rescheduleRotation(LocalTime rotationTime) throws SchedulerException {
		jobScheduler.rescheduleJob(new TriggerKey("rotationTrigger"), createTrigger(rotationTime));
	}
	
	private Trigger createTrigger(LocalTime rotationTime) {
		//0 30 10 ? * * * <--- Everyday at 10:00 AM
		String cronExpression = String.format("0 %s %s ? * * *",
				rotationTime.getMinute(), rotationTime.getHour());
		return newTrigger()
				.withIdentity("rotationTrigger", "design8orGroup")
				.withSchedule(cronSchedule(cronExpression))
				.build();
	}
	
}
