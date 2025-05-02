package com.ss.design8or.service;

import com.ss.design8or.repository.DesignationRepository;
import com.ss.design8or.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author ezerbo
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class StaleRequestHandler {

    public static final String TIMER_NAME = "StaleReqTimer";

    private static final int MILLISECONDS_PER_MINUTE = 60000;

    private static final int TIMER_DELAY = 10000;

    private final ParameterService parameterService;

    private final DesignationRepository designationRepository;

    private final NotificationService notificationService;

    /**
     * Start a timer to check for stale requests
     */
    public void startTimer() {
        long period = parameterService.getParameter().getStaleReqCheckPeriodInMins() * MILLISECONDS_PER_MINUTE;
        new Timer(TIMER_NAME).schedule(new StaleReqTimerTask(designationRepository, notificationService, period), TIMER_DELAY, period);
    }


}
