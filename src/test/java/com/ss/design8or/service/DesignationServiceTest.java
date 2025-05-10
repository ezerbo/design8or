package com.ss.design8or.service;

import com.ss.design8or.config.WebSocketEndpoints;
import com.ss.design8or.controller.response.DesignationAnswer;
import com.ss.design8or.controller.response.DesignationResponse;
import com.ss.design8or.error.exception.ResourceNotFoundException;
import com.ss.design8or.model.*;
import com.ss.design8or.model.enums.DesignationStatus;
import com.ss.design8or.repository.AssignmentRepository;
import com.ss.design8or.repository.DesignationRepository;
import com.ss.design8or.repository.PoolRepository;
import com.ss.design8or.repository.UserRepository;
import com.ss.design8or.service.communication.EmailService;
import com.ss.design8or.service.communication.PushNotificationService;
import org.assertj.core.util.DateUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * @author ezerbo
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class DesignationServiceTest {

    @Autowired
    private PoolRepository poolRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private SimpMessagingTemplate simpMessagingTemplate;

    @MockitoBean
    private PushNotificationService pushNotificationService;

    @Autowired
    private DesignationRepository designationRepository;

    private DesignationService service;

    private PoolService poolService;


    @BeforeEach
    public void init() {
        poolService = new PoolService(poolRepository, userRepository, designationRepository);
        service = new DesignationService(new UserService(userRepository, designationRepository),
                designationRepository, new AssignmentService(assignmentRepository),
                poolService, emailService, pushNotificationService, simpMessagingTemplate);
    }

    @Test
    public void designateCurrentLead() {
        DesignationResponse designationResponse = service.designate(1L);
        assertThat(designationResponse.getMessage()).isEqualTo("User already designated as lead");
        verify(emailService, never()).sendEmail(any());
        verify(simpMessagingTemplate, never())
                .convertAndSend(eq(WebSocketEndpoints.DESIGNATIONS_CHANNEL), any(Designation.class));
    }

    @Test
    public void designateLeadExistingDesignation() {
        DesignationResponse designationResponse = service.designate(2L);
        assertThat(designationResponse.getMessage())
                .isEqualTo("Designation reassigned to user luffy.monkey@onpiece.com");
        verify(emailService, times(1)).sendReassignmentEmail(any());
    }

    @Test
    public void designateLeadNewDesignation() {
        setCurrentDesignationStatus(DesignationStatus.ACCEPTED, new Date());
        DesignationResponse designationResponse = service.designate(3L);
        assertThat(designationResponse.getMessage()).isEqualTo("User successfully designated");
        Optional<Designation> currentDesignationOp = service.getCurrentDesignation();
        assertThat(currentDesignationOp.isPresent()).isTrue();
        Designation currentDesignation = currentDesignationOp.get();
        assertThat(currentDesignation.getStatus()).isEqualByComparingTo(DesignationStatus.PENDING);
        assertThat(currentDesignation.getUser().getId()).isEqualTo(3L);
        verify(emailService, times(1)).sendEmail(any());
        verify(simpMessagingTemplate, times(1))
                .convertAndSend(eq(WebSocketEndpoints.DESIGNATIONS_CHANNEL), any(Designation.class));
    }

    @Test
    public void designateChoosesNewCandidate() {
        setCurrentDesignationStatus(DesignationStatus.ACCEPTED, new Date());
        assertThat(service.getCurrentDesignation()).isNotPresent();
        DesignationResponse designationResponse = service.designate();
        assertThat(designationResponse.getMessage()).isEqualTo("User successfully designated");

        Optional<Designation> currentDesignationOp = service.getCurrentDesignation();
        assertThat(currentDesignationOp.isPresent()).isTrue();
        Designation currentDesignation = currentDesignationOp.get();
        assertThat(currentDesignation.getStatus()).isEqualTo(DesignationStatus.PENDING);

        assertThat(currentDesignation.getUser().getEmailAddress()).isNotEqualTo("chopper.tonytony@onepiece.com");
        verify(emailService, times(1)).sendEmail(any());
        verify(simpMessagingTemplate, times(1))
                .convertAndSend(eq(WebSocketEndpoints.DESIGNATIONS_CHANNEL), any(Designation.class));
    }

    @Test
    public void designateMissingUser() {
        assertThrows(ResourceNotFoundException.class, () -> service.designate(0L));
    }

    @Test
    public void reassignCreatesNewAssignment() {
        Optional<Designation> currentDesignationOp = service.getCurrentDesignation();
        assertThat(currentDesignationOp.isPresent()).isTrue();
        Designation currentDesignation = currentDesignationOp.get();
        assertThat(currentDesignation.getUser().getEmailAddress()).isEqualTo("chopper.tonytony@onepiece.com");
        Designation reassignedDesignation = service.reassign(currentDesignation,
                "luffy.monkey@onpiece.com");
        assertThat(reassignedDesignation.getStatus()).isEqualTo(DesignationStatus.REASSIGNED);
        assertThat(reassignedDesignation.getReassignmentDate()).isToday();
        assertThat(reassignedDesignation.getUser().getEmailAddress()).isEqualTo("luffy.monkey@onpiece.com");
        verify(emailService, times(1)).sendReassignmentEmail(any(User.class));
    }

    @Test
    public void reassignToMissingUser() {
        Optional<Designation> currentDesignationOp = service.getCurrentDesignation();
        assertThat(currentDesignationOp.isPresent()).isTrue();
        Designation currentDesignation = currentDesignationOp.get();
        assertThrows(ResourceNotFoundException.class,
                () -> service.reassign(currentDesignation, "some@test.com"));
    }

    @Test
    public void acceptCreatesNewAssignment() {
        Optional<Designation> currentDesignationOp = service.getCurrentDesignation();
        assertThat(currentDesignationOp.isPresent()).isTrue();
        Designation currentDesignation = currentDesignationOp.get();
        service.accept(currentDesignation, "chopper.tonytony@onepiece.com");
        assertThat(currentDesignation.getStatus()).isEqualTo(DesignationStatus.ACCEPTED);
        assertThat(currentDesignation.getDesignationDate()).isToday();
        AssignmentId assignmentId = AssignmentId.builder()
                .userId(currentDesignation.getUser().getId())
                .poolId(poolService.getCurrent().getId())
                .build();
        assertThat(assignmentRepository.findById(assignmentId)).isPresent();
        verify(simpMessagingTemplate, times(1))
                .convertAndSend(eq(WebSocketEndpoints.ASSIGNMENTS_CHANNEL), any(Assignment.class));
    }

    @Test
    public void candidateDeclinesDesignationAlreadyDeclinedByLead() {
        setCurrentDesignationStatus(DesignationStatus.DECLINED, DateUtil.yesterday());
        Optional<Designation> currentDesignationOp = service.getCurrentDesignation();
        assertThat(currentDesignationOp.isPresent()).isTrue();
        Designation currentDesignation = currentDesignationOp.get();
        Designation designation = service.decline(currentDesignation, "luffy.monkey@onpiece.com");
        assertThat(designation.getUserResponseDate()).isBefore(new Date());
        verify(emailService, never()).broadcastDeclinationEmail(any(), anyList());
        verify(simpMessagingTemplate, never())
                .convertAndSend(eq(WebSocketEndpoints.DESIGNATIONS_CHANNEL), any(Designation.class));
    }

    @Test
    public void leadDeclinesPendingDesignationChangesStatus() {
        Optional<Designation> currentDesignationOp = service.getCurrentDesignation();
        assertThat(currentDesignationOp.isPresent()).isTrue();
        Designation currentDesignation = currentDesignationOp.get();
        assertThat(currentDesignation.getStatus()).isEqualTo(DesignationStatus.PENDING);
        Designation designation = service.decline(currentDesignation, "chopper.tonytony@onepiece.com");
        assertThat(designation.getStatus()).isEqualTo(DesignationStatus.DECLINED);
        assertThat(designation.getUserResponseDate()).isToday();
        verify(emailService, times(1)).broadcastDeclinationEmail(any(), anyList());
        verify(simpMessagingTemplate, times(1))
                .convertAndSend(eq(WebSocketEndpoints.DESIGNATIONS_CHANNEL), any(Designation.class));
    }

    @Test
    public void leadDeclinesAlreadyDeclinedDesignation() {
        setCurrentDesignationStatus(DesignationStatus.DECLINED, DateUtil.yesterday());
        Optional<Designation> currentDesignationOp = service.getCurrentDesignation();
        assertThat(currentDesignationOp.isPresent()).isTrue();
        Designation currentDesignation = currentDesignationOp.get();
        Designation designation = service.decline(currentDesignation, "chopper.tonytony@onepiece.com");
        assertThat(designation.getUserResponseDate()).isBefore(new Date());
        verify(emailService, never()).broadcastDeclinationEmail(any(), anyList());
        verify(simpMessagingTemplate, never())
                .convertAndSend(eq(WebSocketEndpoints.DESIGNATIONS_CHANNEL), any(Designation.class));
    }

    @Test
    public void processResponseForMissingDesignation() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> service.processResponse(0L, "some@test.com", DesignationAnswer.ACCEPT));
        assertThat(exception.getMessage()).isEqualTo("Designation with id 0 not found");
    }

    @Test
    public void processAlreadyProcessedDesignation() {
        DesignationResponse designationResponse = service.processResponse(2L,
                "some@test.com", DesignationAnswer.ACCEPT);
        assertThat(designationResponse.getMessage())
                .isEqualTo("Designation request has already been processed");
    }

    @Test
    public void processStaleDesignationCreatesNewAssignment() {
        setCurrentDesignationStatus(DesignationStatus.STALE, new Date());
        DesignationResponse designationResponse = service.processResponse(1L,
                "luffy.monkey@onpiece.com", DesignationAnswer.ACCEPT);
        runAssignmentCreationAssertions(designationResponse);
    }

    @Test
    public void processReassignedDesignationCreatesNewAssignment() {
        setCurrentDesignationStatus(DesignationStatus.REASSIGNED, new Date());
        DesignationResponse designationResponse = service.processResponse(1L,
                "luffy.monkey@onpiece.com", DesignationAnswer.ACCEPT);
        runAssignmentCreationAssertions(designationResponse);
    }

    @Test
    public void processDeclinedDesignationCreatesNewAssignment() {
        setCurrentDesignationStatus(DesignationStatus.DECLINED, new Date());
        DesignationResponse designationResponse = service.processResponse(1L,
                "luffy.monkey@onpiece.com", DesignationAnswer.ACCEPT);
        runAssignmentCreationAssertions(designationResponse);
    }

    @Test
    public void processPendingDesignationCreatesNewAssignment() {
        Optional<Designation> designationOptional = designationRepository.findOneByStatus(DesignationStatus.PENDING);
        assertThat(designationOptional.isPresent()).isTrue();
        assertThat(designationOptional.get().getUser().getEmailAddress()).isEqualTo("chopper.tonytony@onepiece.com");

        assertThat(assignmentRepository.findById(AssignmentId.builder().userId(1L).poolId(3L).build())).isNotPresent();
        DesignationResponse designationResponse = service.processResponse(1L,
                "chopper.tonytony@onepiece.com", DesignationAnswer.ACCEPT);
        assertThat(designationResponse.getEmailAddress()).isEqualTo("chopper.tonytony@onepiece.com");
        verify(simpMessagingTemplate, times(1))
                .convertAndSend(eq(WebSocketEndpoints.ASSIGNMENTS_CHANNEL), any(Assignment.class));

        assertThat(designationRepository.findOneByStatus(DesignationStatus.PENDING)).isNotPresent();
        assertThat(assignmentRepository.findById(AssignmentId.builder().userId(1L).poolId(3L).build())).isPresent();
        assertThat(designationResponse.getStatus()).isEqualByComparingTo(DesignationStatus.ACCEPTED);
        assertThat(designationResponse.getMessage()).isEqualTo("Designation request successfully processed");
    }

    @Test
    public void processDeclinationResponseBroadcastsToAllCandidates() {
        DesignationResponse designationResponse = service.processResponse(1L,
                "chopper.tonytony@onepiece.com", DesignationAnswer.DECLINE);
        verify(emailService, times(1)).broadcastDeclinationEmail(any(Designation.class), anyList());
        assertThat(designationResponse.getStatus()).isEqualByComparingTo(DesignationStatus.DECLINED);
    }

    private Assignment createAssignment(User user) {
        Pool pool = poolService.getCurrent();
        return Assignment.builder()
                .id(AssignmentId.builder()
                        .userId(user.getId())
                        .poolId(pool.getId())
                        .build())
                .user(user)
                .pool(pool)
                .assignmentDate(new Date())
                .build();
    }

    private void setCurrentDesignationStatus(DesignationStatus status, Date userResponseDate) {
        Optional<Designation> currentDesignationOp = service.getCurrentDesignation();
        assertThat(currentDesignationOp.isPresent()).isTrue();
        Designation currentDesignation = currentDesignationOp.get();
        currentDesignation.setStatus(status);
        currentDesignation.setUserResponseDate(userResponseDate);
        if (DesignationStatus.ACCEPTED.equals(status)) {
            Assignment assignment = createAssignment(currentDesignation.getUser());
            assignmentRepository.save(assignment);
            Pool pool = poolService.getCurrent();
            pool.getAssignments().add(assignment);
            poolRepository.save(pool);
        }
        designationRepository.save(currentDesignation);
    }

    private void runAssignmentCreationAssertions(DesignationResponse designationResponse) {
        assertThat(designationResponse.getStatus()).isEqualTo(DesignationStatus.ACCEPTED);
        assertThat(designationResponse.getMessage()).isEqualTo("Designation request successfully processed");
        assertThat(designationResponse.getDesignationDate()).isToday();
        verify(simpMessagingTemplate, times(1))
                .convertAndSend(eq(WebSocketEndpoints.ASSIGNMENTS_CHANNEL), any(Assignment.class));
    }

}