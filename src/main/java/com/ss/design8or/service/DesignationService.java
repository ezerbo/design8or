package com.ss.design8or.service;

import com.ss.design8or.error.exception.ResourceNotFoundException;
import com.ss.design8or.model.Assignment;
import com.ss.design8or.model.Designation;
import com.ss.design8or.model.DesignationStatus;
import com.ss.design8or.model.User;
import com.ss.design8or.repository.DesignationRepository;
import com.ss.design8or.rest.response.DesignationAnswer;
import com.ss.design8or.rest.response.DesignationResponse;
import com.ss.design8or.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

/**
 * @author ezerbo
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DesignationService {

    private final UserService userService;

    private final DesignationRepository designationRepository;

    private final NotificationService notificationService;

    private final AssignmentService assignmentService;

    private final PoolService poolService;


    /**
     * Designates a user as lead.
     *
     * @param userId Identifier of user to be designated
     *
     * @return Designation response
     */
    public DesignationResponse designate(long userId) {
        Optional<Designation> designationOptional = designationRepository.findOneByStatus(DesignationStatus.PENDING);
        if (designationOptional.isPresent() && designationOptional.get().getUser().getId() == userId) {
            Designation designation = designationOptional.get();
            log.info("Designation {} is already assigned to user {}", designation, userId);
            return buildResponse(designation, "User has a pending designation");
        }
        if (designationOptional.isPresent()) {
            Designation designation = designationOptional.get();
            designation.setStatus(DesignationStatus.REASSIGNED);
            designation.setReassignmentDate(new Date());
            designationRepository.save(designation);
            // TODO Send designation reassignment email
            // The user should no longer be lead
        }
        Designation designation = new Designation();
        designation.setStatus(DesignationStatus.PENDING);
        designation.setUser(userService.findById(userId));
        notificationService.sendDesignationEvent(designation);
        return buildResponse(designation, "User successfully designated");
    }

    /**
     * Process a designation response. Events are emitted in all cases (designation accepted, declined)
     *
     * @return Processed designation
     */
    // TODO Handle case where designation is broadcast and accepted by a different user
    public DesignationResponse processResponse(long id, DesignationAnswer answer) {
        log.info("Processing response for designation with id {}, answer: {}", id, answer);
        return designationRepository.findById(id)
                .map(designation -> {
                    if (!DesignationStatus.PENDING.equals(designation.getStatus())) {
                        log.warn("Designation is not pending {}", designation.getStatus());
                        return buildResponse(designation, "Designation request has already been processed");
                    }
                    if (DesignationAnswer.ACCEPT.equals(answer)) {
                        designation.setStatus(DesignationStatus.ACCEPTED);
                        designation.setUserResponseDate(new Date());
                        Assignment assignment = assignmentService.create(designation.getUser(), poolService.getCurrent());
                        notificationService.sendAssignmentEvent(assignment);
                    } else {
                        //TODO Do not set designation status to declined when a request is stale and has not been declined by the user it was assigned to
                        designation.setStatus(DesignationStatus.DECLINED);
                        notificationService.broadcastDesignationEvents(designation); // Broadcast to all candidates
                    }
                    designation = designationRepository.save(designation);
                    return buildResponse(designation, "Designation request successfully processed");
                })
                .orElseThrow(() -> new ResourceNotFoundException("Designation with id " + id + " not found"));
    }

    private DesignationResponse buildResponse(Designation designation, String message) {
        return DesignationResponse.builder()
                .designationId(designation.getId())
                .designationDate(designation.getDesignationDate())
                .status(designation.getStatus())
                .emailAddress(designation.getUser().getEmailAddress())
                .message(message)
                .build();
    }

    /**
     * Designates the next lead. Typically called by a scheduled Job
     */
    public void designate() {
        log.info("Designating next lead...");
        User lead = poolService.getCurrentPoolCandidates()
                .getFirst();
        log.info("The next lead will be: {}", lead.getEmailAddress());
        designate(lead.getId());
    }

}