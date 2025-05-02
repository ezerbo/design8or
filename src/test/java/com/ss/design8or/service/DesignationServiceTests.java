package com.ss.design8or.service;

import com.ss.design8or.error.exception.ResourceNotFoundException;
import com.ss.design8or.error.exception.UserNotFoundException;
import com.ss.design8or.model.*;
import com.ss.design8or.repository.AssignmentRepository;
import com.ss.design8or.repository.DesignationRepository;
import com.ss.design8or.repository.PoolRepository;
import com.ss.design8or.repository.UserRepository;
import com.ss.design8or.rest.response.DesignationAnswer;
import com.ss.design8or.rest.response.DesignationResponse;
import com.ss.design8or.service.notification.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author ezerbo
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class DesignationServiceTests {

    @Autowired
    private PoolRepository poolRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @MockitoBean
    private NotificationService notificationService;

    @Autowired
    private DesignationRepository designationRepository;

    private DesignationService service;


    @BeforeEach
    public void init() {
        service = new DesignationService(new UserService(userRepository),
                designationRepository, notificationService,
                new AssignmentService(userRepository, assignmentRepository),
                new PoolService(poolRepository, userRepository));
    }

    @Test
    public void designateChoosesNextCandidateAlphabetically() {
//		User designated = service.rotate();
//		assertThat(designated.getEmailAddress()).isEqualTo("robin.nico@onpiece.com");
//		User designatedFromDB = designationRepository.findCurrent().get().getUser();
//		assertThat(designated).isEqualTo(designatedFromDB);
    }

    @Test
    public void designateThrowsUserNotFoundException() {
        assertThrows(UserNotFoundException.class, () -> service.designate(0L));
    }

    @Test
    public void designateSetsCurrentDesignationStatusToAssigned() {
        Optional<Designation> oldPendingDesOp = designationRepository.findOneByStatus(DesignationStatus.PENDING);
        assertThat(oldPendingDesOp.isPresent()).isTrue();
        Designation oldPendingDesignation = oldPendingDesOp.get();
        assertThat(oldPendingDesignation.getStatus()).isEqualByComparingTo(DesignationStatus.PENDING);
        DesignationResponse response = service.designate(2L);
        Optional<Designation> newPendingDesOp = designationRepository.findOneByStatus(DesignationStatus.PENDING);
        assertThat(newPendingDesOp.isPresent()).isTrue();
        Designation newPendingDesignation = newPendingDesOp.get();
//        assertThat(oldPendingDesignation.getStatus()).isEqualByComparingTo(DesignationStatus.REASSIGNED);
        assertThat(oldPendingDesignation.getStatus()).isEqualByComparingTo(DesignationStatus.PENDING);
        assertThat(newPendingDesignation.getStatus()).isEqualByComparingTo(DesignationStatus.PENDING);
        assertThat(response.getEmailAddress()).isSameAs(newPendingDesignation.getUser().getEmailAddress());
        verify(notificationService, times(1)).sendDesignationEvent(newPendingDesignation);
    }

    @Test
    public void processResponseThrowsResourceNotFoundException() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> service.processResponse(0L, DesignationAnswer.ACCEPT));
        assertThat(exception.getMessage()).isEqualTo("Designation with id 0 not found");
    }

    @Test
    public void processResponseReturnsDesignationAlreadyProcessedMessage() {
        DesignationResponse designationResponse = service.processResponse(2L, DesignationAnswer.ACCEPT);
        assertThat(designationResponse.getMessage()).isEqualTo("Designation request has already been processed");
    }

    @Test
    public void processResponseCreatesAssignment() {
        Optional<Designation> designationOptional = designationRepository.findOneByStatus(DesignationStatus.PENDING);
        assertThat(designationOptional.isPresent()).isTrue();
        assertThat(designationOptional.get().getUser().getEmailAddress()).isEqualTo("chopper.tonytony@onepiece.com");

        assertThat(assignmentRepository.findById(AssignmentId.builder().userId(1L).poolId(3L).build())).isNotPresent();
        DesignationResponse designationResponse = service.processResponse(1L, DesignationAnswer.ACCEPT);
        assertThat(designationResponse.getEmailAddress()).isEqualTo("chopper.tonytony@onepiece.com");
        verify(notificationService, times(1)).sendAssignmentEvent(any(Assignment.class));
        assertThat(designationRepository.findOneByStatus(DesignationStatus.PENDING)).isNotPresent();
        assertThat(assignmentRepository.findById(AssignmentId.builder().userId(1L).poolId(3L).build())).isPresent();
        assertThat(designationResponse.getStatus()).isEqualByComparingTo(DesignationStatus.ACCEPTED);
        assertThat(designationResponse.getMessage()).isEqualTo("Designation request successfully processed");
    }

    @Test
    public void processResponseBroadcastDeclinationEvent() {
        DesignationResponse designationResponse = service.processResponse(1L, DesignationAnswer.DECLINE);
        verify(notificationService, times(1)).broadcastDesignationEvents(any(Designation.class));
        assertThat(designationResponse.getStatus()).isEqualByComparingTo(DesignationStatus.DECLINED);
    }

    private Pool getCurrentPool() {
        return poolRepository.findOneByStatus(PoolStatus.STARTED).get();
    }

    @Test
    public void designateCreatesNewPoolOnMissingCurrentPool() {
        userRepository.findAll().stream().filter(u -> !u.isLead())
                .map(u -> assignmentRepository.save(createAssignment(u))).toList(); // <--- Create assignment for all users (lead already has an assignment)
        Pool oldPool = poolRepository.findOneByStatus(PoolStatus.STARTED).get();
        service.designate();
        assertThat(poolRepository.findOneByStatus(PoolStatus.STARTED).get()).isNotSameAs(oldPool);
    }

    private Assignment createAssignment(User user) {
        Pool pool = getCurrentPool();
        return Assignment.builder()
                .id(AssignmentId.builder()
                        .userId(user.getId())
                        .poolId(pool.getId())
                        .build())
                .user(user)
                .pool(pool)
                .build();
    }

    @Test
    public void designateChoosesFromEntireUserBase() {
//		poolRepository.findCurrent().map(pool -> poolRepository.save(pool.end()));// <--- complete current pool
//		User lead = service.rotate();
//		assertThat(lead.getEmailAddress()).isEqualTo("luffy.monkey@onpiece.com");
    }
}