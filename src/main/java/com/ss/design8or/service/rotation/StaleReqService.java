package com.ss.design8or.service.rotation;

import com.ss.design8or.config.properties.ServiceProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

/**
 * @author ezerbo
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class StaleReqService {

    private final ServiceProperties serviceProperties;

    private final StaleReqTimerTask staleReqTimerTask;

    /**
     * Start a timer to check for stale requests
     */
    public void run() {
        long period = TimeUnit.MINUTES.toMillis(serviceProperties.getRotation().getStaleReqCheckTimeInMins());
        Timer staleReqTimer = new Timer("StaleReqTimer");
        staleReqTimer.schedule(staleReqTimerTask, TimeUnit.SECONDS.toMillis(10), period);
    }

}
