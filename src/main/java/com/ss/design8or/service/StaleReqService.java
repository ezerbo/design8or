package com.ss.design8or.service;

import com.ss.design8or.repository.DesignationRepository;
import com.ss.design8or.service.notification.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Timer;

/**
 * @author ezerbo
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class StaleReqService {

    private static final int MILLISECONDS_PER_MINUTE = 60000;

    private static final int TIMER_DELAY = 10000;

    private final ParameterService parameterService;

    private final DesignationRepository designationRepository;

    private final PoolService poolService;

    private final EmailService emailService;

    /**
     * Start a timer to check for stale requests
     */
    public void startTimer() {
        long period = parameterService.getParameter().getStaleReqCheckPeriodInMins() * MILLISECONDS_PER_MINUTE;
        Timer staleReqTimer = new Timer("StaleReqTimer");
        staleReqTimer.schedule(new StaleReqTimerTask(designationRepository, emailService, poolService, period),
                TIMER_DELAY, period);
    }

}
