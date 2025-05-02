package com.ss.design8or.service;

import com.ss.design8or.config.properties.ServiceProperties;
import com.ss.design8or.service.job.RotationJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

import static com.ss.design8or.service.Design8orUtil.formatRotationTime;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author ezerbo
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RotationService {

	private static final String SCHEDULER_GROUP = "designationRotation";

	private static final String ROTATION_JOB = "designationRotationJob";

	private static final String ROTATION_TRIGGER = "designationRotationTrigger";


	private final Scheduler jobScheduler;

	private final ServiceProperties serviceProperties;

	public void reschedule(LocalTime rotationTime) {
		JobKey jobKey = JobKey.jobKey(ROTATION_JOB, SCHEDULER_GROUP);
		TriggerKey triggerKey = TriggerKey.triggerKey(ROTATION_TRIGGER, SCHEDULER_GROUP);
		try {
			if (jobScheduler.checkExists(triggerKey)) {
				log.info("Rescheduling existing rotation trigger to '{}'", formatRotationTime(rotationTime));
				jobScheduler.rescheduleJob(triggerKey, createTrigger(rotationTime));
			} else {
				log.info("Scheduling new rotation job at '{}'", formatRotationTime(rotationTime));
				JobDetail jobDetail = JobBuilder.newJob(RotationJob.class)
						.withIdentity(jobKey)
						.build();
				jobScheduler.scheduleJob(jobDetail, createTrigger(rotationTime));
			}
		} catch (SchedulerException e) {
			log.error("Failed to schedule/reschedule rotation job. Rotation time: {}", rotationTime, e);
			throw new RuntimeException(e);
		}

	}

	private Trigger createTrigger(LocalTime rotationTime) {
		//0 30 10 ? * * * <--- Everyday at 10:00 AM
		String cronExpression = String.format(serviceProperties.getRotation().getCronExpression(),
				rotationTime.getMinute(), rotationTime.getHour());
		return newTrigger()
				.forJob(ROTATION_JOB, SCHEDULER_GROUP)
				.withIdentity(ROTATION_TRIGGER, SCHEDULER_GROUP)
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
				.build();
	}
	
}
