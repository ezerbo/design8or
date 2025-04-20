package com.ss.design8or.service;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static com.ss.design8or.service.Design8orUtil.formatRotationTime;

import java.time.LocalTime;

import lombok.RequiredArgsConstructor;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Component;

import com.ss.design8or.service.job.RotationJob;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author ezerbo
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RotationService {

	private final Scheduler jobScheduler;
	
	public void scheduleRotation(LocalTime rotationTime)  {
        try {
			Trigger trigger = createTrigger(rotationTime);
			JobDetail jobDetail = newJob(RotationJob.class)
					.withIdentity("rotationJob", "design8orGroup")
					.build();
            jobScheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
			log.error("Unable to schedule rotation job. Rotation time: {}", rotationTime, e);
            throw new RuntimeException(e);
        }
    }

	//TODO use single method to schedule and reschedule rotation jobs
	public void rescheduleRotation(LocalTime rotationTime) {
        try {
			log.info("Rescheduling rotation, setting time to '{}'", formatRotationTime(rotationTime));
			jobScheduler.rescheduleJob(TriggerKey.triggerKey("rotationTrigger","design8orGroup"),
                    createTrigger(rotationTime));
        } catch (SchedulerException e) {
			log.error("Unable to reschedule rotation job. Rotation time: {}", rotationTime, e);
            throw new RuntimeException(e);
        }
    }

	// TODO Move Cron expression to config file
	private Trigger createTrigger(LocalTime rotationTime) {
		//0 30 10 ? * * * <--- Everyday at 10:00 AM
		String cronExpression = String.format("0 %s %s ? * * *",
				rotationTime.getMinute(), rotationTime.getHour());
		return newTrigger()
				.forJob("rotationJob", "design8orGroup")
				.withIdentity("rotationTrigger", "design8orGroup")
				.withSchedule(cronSchedule(cronExpression))
				.build();
	}
	
}
