package com.ss.design8or.service;

import com.ss.design8or.config.WebSocketEndpoints;
import com.ss.design8or.error.exception.ResourceNotFoundException;
import com.ss.design8or.model.*;
import com.ss.design8or.repository.DesignationRepository;
import com.ss.design8or.rest.response.DesignationAnswer;
import com.ss.design8or.rest.response.DesignationResponse;
import com.ss.design8or.service.notification.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
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

    private final AssignmentService assignmentService;

    private final PoolService poolService;

    private final EmailService emailService;

    private final SimpMessagingTemplate simpMessagingTemplate;


    /**
     * Designates a user as the lead.
     *
     * @param userId Identifier of the user to be designated
     *
     * @return Designation response
     */
    public DesignationResponse designate(long userId) {
        Optional<Designation> designationOptional = getCurrentDesignation();
        if (designationOptional.isPresent() && designationOptional.get().getUser().getId() == userId) {
            Designation designation = designationOptional.get();
            log.info("The current designation is already assigned to '{}'", designation.getUser().getEmailAddress());
            return buildResponse(designation, "User already designated as lead");
        }
        User user = userService.findById(userId);
        if (designationOptional.isPresent()) {
            Designation designation = reassign(designationOptional.get(), user.getEmailAddress());
            log.info("Designation reassigned, the new designated user is '{}'", user.getEmailAddress());
            return buildResponse(designation, "Designation reassigned to user " + user.getEmailAddress());
        }
        Designation designation = Designation.builder()
                .status(DesignationStatus.PENDING)
                .user(user)
                .designationDate(new Date())
                .build();
        designation = designationRepository.save(designation);
        emailService.sendEmail(designation);
        simpMessagingTemplate.convertAndSend(WebSocketEndpoints.DESIGNATIONS_CHANNEL, designation);
        log.info("The new designated user is '{}'", user.getEmailAddress());
        return buildResponse(designation, "User successfully designated");
    }
    /**
     * Processes a response to a designation request.
     *
     * @param id Identifier of the designation
     * @param emailAddress Email address of the user responding
     * @param answer The answer to the designation request (ACCEPT or DECLINE)
     *
     * @return Designation response
     */
    public DesignationResponse processResponse(long id, String emailAddress, DesignationAnswer answer) {
        log.info("Processing response for designation with id {}, answer: {}", id, answer);
        return designationRepository.findById(id)
                .map(designation -> {
                    if (DesignationStatus.ACCEPTED.equals(designation.getStatus())) {
                        log.warn("Designation has already been processed {}", designation.getStatus());
                        return buildResponse(designation, "Designation request has already been processed");
                    }
                    designation = DesignationAnswer.ACCEPT.equals(answer) ? accept(designation, emailAddress)
                            : decline(designation, emailAddress);
                    return buildResponse(designation, "Designation request successfully processed");
                })
                .orElseThrow(() -> new ResourceNotFoundException("Designation with id " + id + " not found"));
    }

    /**
     * Reassigns a designation to a new user.
     *
     * @param designation The designation to be reassigned
     * @param emailAddress The email address of the new assignee
     *
     * @return The updated designation
     */
    public Designation reassign(Designation designation, String emailAddress) {
        User currentAssignee = designation.getUser();
        designation = designation.toBuilder()
                .user(userService.findByEmailAddress(emailAddress))
                .status(DesignationStatus.REASSIGNED)
                .reassignmentDate(new Date())
                .build();
        emailService.sendReassignmentEmail(currentAssignee);
        emailService.sendEmail(designation);
        return designationRepository.save(designation);
    }

    /**
     * Accepts a designation, marking it as accepted and creating an assignment.
     *
     * @param designation  The designation to be accepted
     * @param emailAddress The email address of the user accepting the designation
     * @return The updated designation
     */
    public Designation accept(Designation designation, String emailAddress) {
        if (!designation.getUser().getEmailAddress().equals(emailAddress)) {
            designation = reassign(designation, emailAddress);
            log.info("Designation reassigned to user with email: {}", emailAddress);
        }
        designation.setStatus(DesignationStatus.ACCEPTED);
        designation.setUserResponseDate(new Date());
        Assignment assignment = assignmentService.create(designation.getUser(), poolService.getCurrent());
        simpMessagingTemplate.convertAndSend(WebSocketEndpoints.ASSIGNMENTS_CHANNEL, assignment);
        return designationRepository.save(designation);
    }

    /**
     * Declines a designation, marking it as declined and notifying all candidates.
     *
     * @param designation The designation to be declined
     * @param emailAddress The email address of the user declining the designation
     *
     * @return The updated designation
     */
    public Designation decline(Designation designation, String emailAddress) {
        if (DesignationStatus.DECLINED.equals(designation.getStatus())
                || !designation.getUser().getEmailAddress().equals(emailAddress)) {
            log.warn("Designation has already been declined or hasn't been declined by the current lead");
            return designation;
        }
        designation.setStatus(DesignationStatus.DECLINED);
        designation.setUserResponseDate(new Date());
        simpMessagingTemplate.convertAndSend(WebSocketEndpoints.DESIGNATIONS_CHANNEL, designation);
        emailService.broadcastDeclinationEmail(designation, poolService.getCurrentPoolCandidates());
        return designationRepository.save(designation);
    }

    private DesignationResponse buildResponse(Designation designation, String message) {
        return DesignationResponse.builder()
                .id(designation.getId())
                .designationDate(designation.getDesignationDate())
                .status(designation.getStatus())
                .emailAddress(designation.getUser().getEmailAddress())
                .message(message)
                .build();
    }

    public DesignationResponse designate() {
        log.info("Designating next lead...");
        return designate(poolService.getCurrentPoolCandidates()
                .getFirst().getId());
    }

    public Optional<Designation> getCurrentDesignation() {
        return designationRepository.findOneByStatusNotIn(List.of(DesignationStatus.ACCEPTED));
    }

}