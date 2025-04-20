package com.ss.design8or.service;

import com.ss.design8or.error.exception.DesignationNotFoundException;
import com.ss.design8or.error.exception.InvalidDesignationResponseException;
import com.ss.design8or.error.exception.UserNotFoundException;
import com.ss.design8or.model.*;
import com.ss.design8or.repository.AssignmentRepository;
import com.ss.design8or.repository.DesignationRepository;
import com.ss.design8or.repository.PoolRepository;
import com.ss.design8or.repository.UserRepository;
import com.ss.design8or.rest.response.DesignationResponse;
import com.ss.design8or.service.notification.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author ezerbo
 *
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
		AssignmentService assignmentService = new AssignmentService(userRepository, assignmentRepository);
		service = new DesignationService(poolRepository, userRepository,
				designationRepository, notificationService, assignmentService);
	}

	@Test
	public void designateChoosesNextCandidateAlphabetically() {
		User designated = service.designate();
		assertThat(designated.getEmailAddress()).isEqualTo("robin.nico@onpiece.com");
		User designatedFromDB = designationRepository.findCurrent().get().getUser();
		assertThat(designated).isEqualTo(designatedFromDB);
	}
	
	@Test
	public void designateThrowsUserNotFoundException() {
		assertThrows(UserNotFoundException.class, () -> service.designate(User.builder().id(0L).build()));
	}
	
	@Test
	public void designateSetsCurrentDesignationStatusToAssigned() {
		Designation oldPendingDesignation = designationRepository.findCurrent().get();
		assertTrue(oldPendingDesignation.isPending());
		User user = service.designate(User.builder().id(2L).build());
		Designation newPendingDesignation = designationRepository.findCurrent().get();
		assertThat(oldPendingDesignation.getStatus()).isEqualByComparingTo(DesignationStatus.REASSIGNED);
		assertFalse(oldPendingDesignation.isPending());
		assertThat(newPendingDesignation.getStatus()).isEqualByComparingTo(DesignationStatus.PENDING);
		assertTrue(newPendingDesignation.isPending());
		assertThat(user).isSameAs(newPendingDesignation.getUser());
		verify(notificationService, times(1)).emitDesignationEvent(newPendingDesignation);
	}
	
	@Test
	public void processDesignationResponseThrowsInvalidResponse() {
		assertThrows(InvalidDesignationResponseException.class,
				() -> service.processDesignationResponse(DesignationResponse.builder().response("unknow").build()));
	}
	
	@Test
	public void processDesignationResponseThrowsUserNotFoundException() {
		DesignationResponse response = DesignationResponse.builder()
				.response("accept")
				.emailAddress("test@test.com")
				.token("2cSMV4FoSBwSHTOxG6uIilAOlmpbhEG0")
				.build();
		UserNotFoundException exception = assertThrows(UserNotFoundException.class,
				() -> service.processDesignationResponse(response));
		assertThat(exception.getMessage()).isEqualTo("No user found with email address: 'test@test.com'");
	}
	
	@Test
	public void processDesignationResponseThrowsDesignationNotFoundException() {
		DesignationResponse response = DesignationResponse.builder()
				.response("accept")
				.emailAddress("sandji.vinsmoke@onpiece.com")
				.token("2cSMV4FoSBwSHTOxG6uIilAOlmpbhEG0TzIAUGBskX+Z3zbnbFmwYw==")
				.build();
		DesignationNotFoundException exception = assertThrows(DesignationNotFoundException.class,
				() -> service.processDesignationResponse(response));
		assertThat(exception.getMessage())
				.isEqualTo("No designation Found. It may have already been accepted.");
	}
	
	@Test
	public void processDesignationResponseCreatesAssignment() {
		assertThat(designationRepository.findCurrent().get().getUser().getEmailAddress()).isEqualTo("chopper.tonytony@onepiece.com");
		assertThat(assignmentRepository.findById(AssignmentId.builder().userId(1L).poolId(3L).build())).isNotPresent();
		DesignationResponse response = DesignationResponse.builder()
				.response("accept")
				.emailAddress("chopper.tonytony@onepiece.com")
				.token("Vqiy3c3Z4uGm7rk8SECupeNKoOHeZ7")
				.build();
		Designation designation = service.processDesignationResponse(response);
		assertThat(designation.getUser().getEmailAddress()).isEqualTo("chopper.tonytony@onepiece.com");
		verify(notificationService, times(1)).emitAssignmentEvent(any(Assignment.class));
		assertThat(designationRepository.findCurrent()).isNotPresent();
		assertThat(assignmentRepository.findById(AssignmentId.builder().userId(1L).poolId(3L).build())).isPresent();
		assertThat(designation.getStatus()).isEqualByComparingTo(DesignationStatus.ACCEPTED);
	}
	
	@Test
	public void processDesignationResponseBroadcastDeclinationEvent() {
		DesignationResponse response = DesignationResponse.builder()
				.response("decline")
				.emailAddress("chopper.tonytony@onepiece.com")
				.token("Vqiy3c3Z4uGm7rk8SECupeNKoOHeZ7") //<----- chopper.tonytony@onepiece.com
				.build();
		Designation designation = service.processDesignationResponse(response);
		verify(notificationService, times(1)).emitDesignationEvent(eq(designation), any());
		assertThat(designation.getStatus()).isEqualByComparingTo(DesignationStatus.DECLINED);
	}
	
	private Pool getCurrentPool() {
		return poolRepository.findCurrent().get();
	}
	
	@Test
	public void validateDesignationResponseThrowsException() throws Exception {
		Method validateResponse = DesignationService.class.getDeclaredMethod("validateResponse", String.class);
		validateResponse.setAccessible(true);
		try {
			validateResponse.invoke(service, "unknown");
			fail("Expected exception to be throwned");
		} catch (InvocationTargetException e) {
			assertThat(e.getCause().getMessage()).isEqualTo("No such designation response 'unknown'");
		}
	}
	
	@Test
	public void validateDesignationResponseWithAccept() throws Exception {
		Method validateResponse = DesignationService.class.getDeclaredMethod("validateResponse", String.class);
		validateResponse.setAccessible(true);
		validateResponse.invoke(service, "accept");
	}
	
	@Test
	public void validateDesignationResponseDecline() throws Exception {
		Method validateResponse = DesignationService.class.getDeclaredMethod("validateResponse", String.class);
		validateResponse.setAccessible(true);
		validateResponse.invoke(service, "decline");
	}

	@Test
	public void designateCreatesNewPoolOnMissingCurrentPool() {
		System.out.println("Tests : " + userRepository.count());
		userRepository.findAll().stream().filter(u -> !u.isLead())
			.map(u -> assignmentRepository.save(createAssignment(u))).toList(); // <--- Create assignment for all users (lead already has an assignment)
		Pool oldPool = poolRepository.findCurrent().get();
		service.designate();
		assertThat(poolRepository.findCurrent().get()).isNotSameAs(oldPool);
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
		poolRepository.findCurrent().map(pool -> poolRepository.save(pool.end()));// <--- complete current pool
		User lead = service.designate();
		assertThat(lead.getEmailAddress()).isEqualTo("luffy.monkey@onpiece.com");
	}
}