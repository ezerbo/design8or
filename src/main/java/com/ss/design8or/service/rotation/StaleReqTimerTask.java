package com.ss.design8or.service.rotation;

import com.ss.design8or.config.properties.ServiceProperties;
import com.ss.design8or.model.enums.DesignationStatus;
import com.ss.design8or.repository.DesignationRepository;
import com.ss.design8or.service.PoolService;
import com.ss.design8or.service.communication.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class StaleReqTimerTask extends TimerTask {

    private final DesignationRepository designationRepository;

    private final EmailService emailService;

    private final PoolService poolService;

    private final ServiceProperties serviceProperties;

    @Override
    public void run() {
        long period = TimeUnit.MINUTES.toMillis(serviceProperties.getRotation().getStaleReqCheckTimeInMins());
        log.info("Checking for stale designation requests");
        designationRepository.findOneByStatus(DesignationStatus.PENDING).ifPresent(designation -> {
            long elapsedTime = new Date().getTime() - designation.getDesignationDate().getTime();
            if (elapsedTime >= period) {
                log.info("Stale designation request found, sending notifications to all candidates...");
                designation.setStatus(DesignationStatus.STALE);
                designationRepository.save(designation);
                emailService.broadcastDeclinationEmail(designation, poolService.getCurrentPoolCandidates());
            }
        });
    }
}