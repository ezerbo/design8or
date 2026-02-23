package com.ss.design8or.service;

import com.ss.design8or.config.WebSocketEndpoints;
import com.ss.design8or.error.exception.DesignationAlreadyClaimedException;
import com.ss.design8or.error.exception.ResourceNotFoundException;
import com.ss.design8or.model.Assignment;
import com.ss.design8or.model.Pool;
import com.ss.design8or.model.User;
import com.ss.design8or.model.enums.DesignationStatus;
import com.ss.design8or.model.enums.DesignationType;
import com.ss.design8or.repository.AssignmentRepository;
import com.ss.design8or.controller.response.DesignationAnswer;
import com.ss.design8or.controller.response.DesignationResponse;
import com.ss.design8or.service.communication.EmailService;
import com.ss.design8or.service.communication.PushNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    private final AssignmentRepository assignmentRepository;

    private final PoolService poolService;

    private final EmailService emailService;

    private final PushNotificationService pushNotificationService;

    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Broadcasts designation to all eligible users in the pool (automatic broadcast model).
     * First user to accept wins.
     *
     * @param pool The pool to designate leads for
     * @return List of assignments that were designated
     */
    public List<Assignment> designateAll(Pool pool) {
        log.info("Broadcasting designation to all eligible users in pool {}", pool.getId());

        // Find all assignments without designation status (never designated)
        List<Assignment> eligibleAssignments = assignmentRepository.findByPoolAndDesignationStatusIsNull(pool);

        if (eligibleAssignments.isEmpty()) {
            log.warn("No eligible users found for designation in pool {}", pool.getId());
            return List.of();
        }

        // Set all to PENDING with AUTOMATIC type
        LocalDateTime now = LocalDateTime.now();
        eligibleAssignments.forEach(assignment -> {
            assignment.setDesignationStatus(DesignationStatus.PENDING);
            assignment.setDesignationType(DesignationType.AUTOMATIC);
            assignment.setDesignatedAt(now);
        });

        List<Assignment> savedAssignments = assignmentRepository.saveAll(eligibleAssignments);

        // Send emails and broadcast via WebSocket
        savedAssignments.forEach(assignment -> {
            emailService.sendEmail(assignment, assignment.getUser());
            simpMessagingTemplate.convertAndSend(WebSocketEndpoints.DESIGNATIONS_CHANNEL, assignment);
        });

        log.info("Designated {} users for pool {}", savedAssignments.size(), pool.getId());
        return savedAssignments;
    }

    /**
     * Accepts a designation. The user becomes the new lead while previous lead remains as participant.
     * All other pending designations are invalidated.
     *
     * @param assignmentId The ID of the assignment to accept
     * @return The accepted assignment
     */
    public Assignment accept(Long assignmentId) {
        log.info("Processing accept for assignment {}", assignmentId);

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment with id " + assignmentId + " not found"));

        // Verify assignment is PENDING
        if (assignment.getDesignationStatus() != DesignationStatus.PENDING) {
            log.warn("Assignment {} is not PENDING, current status: {}", assignmentId, assignment.getDesignationStatus());
            throw new DesignationAlreadyClaimedException("This designation is no longer available");
        }

        // Accept this assignment - old lead remains as ACCEPTED participant
        assignment.setDesignationStatus(DesignationStatus.ACCEPTED);
        assignment.setRespondedAt(LocalDateTime.now());
        assignment = assignmentRepository.saveAndFlush(assignment);

        // Invalidate all other pending designations for this pool
        assignmentRepository.invalidatePendingDesignations(assignment.getPool().getId());

        // Broadcast and notify
        simpMessagingTemplate.convertAndSend(WebSocketEndpoints.DESIGNATIONS_CHANNEL, assignment);
        pushNotificationService.sendAssignmentNotification(assignment.getUser().getEmailAddress());

        log.info("Assignment {} accepted by user {}, now the current lead", assignmentId, assignment.getUser().getEmailAddress());
        return assignment;
    }

    /**
     * Manually designates a specific user as lead (bypasses broadcast model).
     * Previous lead remains as participant.
     *
     * @param assignmentId The ID of the assignment to manually designate
     * @return The designated assignment
     */
    public Assignment designateManually(Long assignmentId) {
        log.info("Processing manual designation for assignment {}", assignmentId);

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment with id " + assignmentId + " not found"));

        // Manually designate as ACCEPTED with MANUAL type - old lead remains as ACCEPTED participant
        LocalDateTime now = LocalDateTime.now();
        assignment.setDesignationStatus(DesignationStatus.ACCEPTED);
        assignment.setDesignationType(DesignationType.MANUAL);
        assignment.setDesignatedAt(now);
        assignment.setRespondedAt(now);
        assignment = assignmentRepository.saveAndFlush(assignment);

        // Invalidate any pending designations
        assignmentRepository.invalidatePendingDesignations(assignment.getPool().getId());

        // Broadcast and notify
        simpMessagingTemplate.convertAndSend(WebSocketEndpoints.DESIGNATIONS_CHANNEL, assignment);
        pushNotificationService.sendAssignmentNotification(assignment.getUser().getEmailAddress());

        log.info("Assignment {} manually designated as lead", assignmentId);
        return assignment;
    }

    /**
     * Declines a designation.
     *
     * @param assignmentId The ID of the assignment to decline
     * @return The declined assignment
     */
    public Assignment decline(Long assignmentId) {
        log.info("Processing decline for assignment {}", assignmentId);

        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment with id " + assignmentId + " not found"));

        if (assignment.getDesignationStatus() != DesignationStatus.PENDING) {
            log.warn("Assignment {} is not PENDING, current status: {}", assignmentId, assignment.getDesignationStatus());
            throw new IllegalStateException("Cannot decline assignment that is not PENDING");
        }

        assignment.setDesignationStatus(DesignationStatus.DECLINED);
        assignment.setRespondedAt(LocalDateTime.now());
        assignment = assignmentRepository.save(assignment);

        // Broadcast via WebSocket and push notification
        simpMessagingTemplate.convertAndSend(WebSocketEndpoints.DESIGNATIONS_CHANNEL, assignment);
        pushNotificationService.sendDeclineNotification(assignment.getUser().getEmailAddress());

        log.info("Assignment {} declined by user {}", assignmentId, assignment.getUser().getEmailAddress());
        return assignment;
    }

    /**
     * Processes a response to a designation request (for backward compatibility with email links).
     *
     * @param id Identifier of the assignment
     * @param emailAddress Email address of the user responding
     * @param answer The answer to the designation request (ACCEPT or DECLINE)
     *
     * @return Designation response
     */
    public DesignationResponse processResponse(long id, String emailAddress, DesignationAnswer answer) {
        log.info("Processing response for assignment with id {}, answer: {}", id, answer);

        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment with id " + id + " not found"));

        // Verify email address matches
        if (!assignment.getUser().getEmailAddress().equalsIgnoreCase(emailAddress)) {
            log.warn("Email address mismatch for assignment {}", id);
            throw new IllegalArgumentException("Email address does not match assignment");
        }

        if (DesignationAnswer.ACCEPT.equals(answer)) {
            assignment = accept(id);
            return buildResponse(assignment, "Designation request accepted");
        } else {
            assignment = decline(id);
            return buildResponse(assignment, "Designation request declined");
        }
    }

    /**
     * Sends a designation request to a specific user by user ID.
     * The user must accept before becoming the lead.
     *
     * @param userId Identifier of the user to be designated
     * @return Designation response
     */
    public DesignationResponse designate(long userId) {
        log.info("Sending designation request to user with id {}...", userId);

        User user = userService.findById(userId);
        Pool currentPool = poolService.getCurrent();

        // Find or create assignment for this user in current pool
        List<Assignment> userAssignments = assignmentRepository.findByPool(currentPool).stream()
                .filter(a -> a.getUser().getId().equals(userId))
                .toList();

        Assignment assignment;
        if (userAssignments.isEmpty()) {
            // Create new assignment
            assignment = Assignment.builder()
                    .pool(currentPool)
                    .user(user)
                    .assignmentDate(LocalDateTime.now())
                    .build();
            assignment = assignmentRepository.save(assignment);
        } else {
            assignment = userAssignments.get(0);
        }

        // Set as PENDING designation with MANUAL type
        LocalDateTime now = LocalDateTime.now();
        assignment.setDesignationStatus(DesignationStatus.PENDING);
        assignment.setDesignationType(DesignationType.MANUAL);
        assignment.setDesignatedAt(now);
        assignment = assignmentRepository.save(assignment);

        // Send email and broadcast via WebSocket
        emailService.sendEmail(assignment, user);
        simpMessagingTemplate.convertAndSend(WebSocketEndpoints.DESIGNATIONS_CHANNEL, assignment);

        log.info("Designation request sent to user {}", user.getEmailAddress());
        return buildResponse(assignment, "Designation request sent to user");
    }

    /**
     * Designates a random user from the current pool of candidates.
     *
     * @return Designation response
     */
    public DesignationResponse designate() {
        log.info("Designating next lead...");
        Pool currentPool = poolService.getCurrent();
        List<Assignment> eligibleAssignments = assignmentRepository.findByPoolAndDesignationStatusIsNull(currentPool);

        if (eligibleAssignments.isEmpty()) {
            throw new IllegalStateException("No eligible users for designation");
        }

        // Designate all and let first to accept win
        designateAll(currentPool);
        return DesignationResponse.builder()
                .message("Designation broadcast to " + eligibleAssignments.size() + " users")
                .build();
    }

    public Optional<Assignment> getCurrentDesignation() {
        Pool currentPool = poolService.getCurrent();
        return assignmentRepository.findFirstByPoolAndDesignationStatusOrderByRespondedAtDesc(
                currentPool, DesignationStatus.ACCEPTED);
    }

    public Page<Assignment> findAll(Pageable pageable) {
        List<Assignment> allAssignments = assignmentRepository.findAll();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allAssignments.size());
        return new PageImpl<>(allAssignments.subList(start, end), pageable, allAssignments.size());
    }

    private DesignationResponse buildResponse(Assignment assignment, String message) {
        return DesignationResponse.builder()
                .id(assignment.getId())
                .designationDate(assignment.getDesignatedAt() != null ?
                        java.sql.Timestamp.valueOf(assignment.getDesignatedAt()) : new Date())
                .status(assignment.getDesignationStatus())
                .emailAddress(assignment.getUser().getEmailAddress())
                .message(message)
                .build();
    }
}
