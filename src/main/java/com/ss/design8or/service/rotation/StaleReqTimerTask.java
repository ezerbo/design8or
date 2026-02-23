package com.ss.design8or.service.rotation;

import com.ss.design8or.config.properties.ServiceProperties;
import com.ss.design8or.model.Assignment;
import com.ss.design8or.model.Pool;
import com.ss.design8or.model.enums.DesignationStatus;
import com.ss.design8or.repository.AssignmentRepository;
import com.ss.design8or.service.DesignationService;
import com.ss.design8or.service.PoolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.TimerTask;

@Slf4j
@RequiredArgsConstructor
@Component
public class StaleReqTimerTask extends TimerTask {

    private final AssignmentRepository assignmentRepository;

    private final DesignationService designationService;

    private final PoolService poolService;

    private final ServiceProperties serviceProperties;

    @Override
    public void run() {
        long staleThresholdMinutes = serviceProperties.getRotation().getStaleReqCheckTimeInMins();
        log.info("Checking for stale designation requests (threshold: {} minutes)", staleThresholdMinutes);

        try {
            Pool currentPool = poolService.getCurrent();
            List<Assignment> pendingAssignments = assignmentRepository.findByPoolAndDesignationStatus(
                    currentPool, DesignationStatus.PENDING);

            LocalDateTime now = LocalDateTime.now();

            for (Assignment assignment : pendingAssignments) {
                if (assignment.getDesignatedAt() != null) {
                    long elapsedMinutes = ChronoUnit.MINUTES.between(assignment.getDesignatedAt(), now);

                    if (elapsedMinutes >= staleThresholdMinutes) {
                        log.info("Stale designation request found for assignment {}, invalidating...", assignment.getId());
                        designationService.decline(assignment.getId());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error checking for stale designation requests", e);
        }
    }
}
