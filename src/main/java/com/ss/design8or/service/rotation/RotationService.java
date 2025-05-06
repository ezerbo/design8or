package com.ss.design8or.service.rotation;

import com.cronutils.descriptor.CronDescriptor;
import com.cronutils.model.Cron;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import com.ss.design8or.config.properties.ServiceProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.util.Locale;

import static com.cronutils.model.CronType.QUARTZ;

/**
 * @author ezerbo
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

    public void schedule() {
        try {
            log.info("Scheduling rotation job, frequency: '{}'", cronExpressionDescription());
            JobDetail jobDetail = JobBuilder.newJob(RotationJob.class)
                    .withIdentity(JobKey.jobKey(ROTATION_JOB, SCHEDULER_GROUP))
                    .build();
            CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                    .forJob(ROTATION_JOB, SCHEDULER_GROUP)
                    .withIdentity(ROTATION_TRIGGER, SCHEDULER_GROUP)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression()))
                    .build();
            jobScheduler.scheduleJob(jobDetail, cronTrigger);
        } catch (SchedulerException e) {
            log.error("Failed to schedule rotation job", e);
            throw new RuntimeException(e);
        }

    }

    private String cronExpression() {
        return serviceProperties.getRotation().getCronExpression();
    }

    private String cronExpressionDescription() {
        Cron cron = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(QUARTZ))
                .parse(cronExpression());
        return CronDescriptor.instance(Locale.US).describe(cron);
    }

}
