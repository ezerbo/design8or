package com.ss.design8or.service;

import com.ss.design8or.model.DesignationStatus;
import com.ss.design8or.repository.DesignationRepository;
import com.ss.design8or.service.notification.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.TimerTask;

@Slf4j
@RequiredArgsConstructor
public class StaleReqTimerTask extends TimerTask {

    private final DesignationRepository designationRepository;

    private final EmailService emailService;

    private final PoolService poolService;

    private final long checkInterval;

    @Override
        public void run() {
            log.info("Checking for stale designation requests");
            designationRepository.findOneByStatus(DesignationStatus.PENDING).ifPresent(designation -> {
                long elapsedTime = new Date().getTime() - designation.getDesignationDate().getTime();
                if (elapsedTime >= checkInterval) {
                    log.info("Stale designation request found, sending notifications to all candidates...");
                    designation.setStatus(DesignationStatus.STALE);
                    designationRepository.save(designation);
                    emailService.broadcastDeclinationEmail(designation, poolService.getCurrentPoolCandidates());
                }
            }); 
        }
    }