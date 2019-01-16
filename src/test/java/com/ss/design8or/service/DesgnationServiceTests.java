package com.ss.design8or.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.ss.design8or.error.DesignationNotFoundException;
import com.ss.design8or.error.InvalidDesignationResponseException;
import com.ss.design8or.error.UserNotFoundException;
import com.ss.design8or.model.Assignment;
import com.ss.design8or.model.AssignmentId;
import com.ss.design8or.model.Designation;
import com.ss.design8or.model.DesignationResponse;
import com.ss.design8or.model.DesignationStatus;
import com.ss.design8or.model.Pool;
import com.ss.design8or.model.User;
import com.ss.design8or.repository.AssignmentRepository;
import com.ss.design8or.repository.DesignationRepository;
import com.ss.design8or.repository.PoolRepository;
import com.ss.design8or.repository.UserRepository;
import com.ss.design8or.service.notification.NotificationService;

/**
 * @author ezerbo
 *
 */
@DataJpaTest
@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class DesgnationServiceTests {

	@Autowired
	private PoolRepository poolRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AssignmentRepository assignmentRepository;

	@MockBean
	private NotificationService notificationService;

	@Autowired
	private DesignationRepository designationRepository;

	private DesignationService service;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
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
	
	@Test(expected = UserNotFoundException.class)
	public void designateThrowsUserNotFoundException() {
		service.designate(User.builder().id(0L).build());
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
		assertThat(newPendingDesignation.isPending());
		assertThat(user).isSameAs(newPendingDesignation.getUser());
		verify(notificationService, times(1)).emitDesignationEvent(newPendingDesignation);
	}
	
	@Test(expected = InvalidDesignationResponseException.class)
	public void processDesignationResponseThrowsInvalidResponse() {
		service.processDesignationResponse(DesignationResponse.builder().response("unknow").build());
	}
	
	@Test
	public void processDesignationResponseThrowsUserNotFoundException() {
		expectedException.expect(UserNotFoundException.class);
		expectedException.expectMessage("No user found with email address: 'test@test.com'");
		DesignationResponse response = DesignationResponse.builder()
				.response("accept")
				.emailAddress("test@test.com")
				.token("2cSMV4FoSBwSHTOxG6uIilAOlmpbhEG0")
				.build();
		service.processDesignationResponse(response);
	}
	
	@Test
	public void processDesignationResponseThrowsDesignationNotFoundException() {
		expectedException.expect(DesignationNotFoundException.class);
		expectedException.expectMessage("No designation found for 'sandji.vinsmoke@onpiece.com'");
		DesignationResponse response = DesignationResponse.builder()
				.response("accept")
				.emailAddress("sandji.vinsmoke@onpiece.com")
				.token("2cSMV4FoSBwSHTOxG6uIilAOlmpbhEG0TzIAUGBskX+Z3zbnbFmwYw==")
				.build();
		service.processDesignationResponse(response);
	}
	
	@Test
	public void processDesignationResponseCreatesAssignment() {
		assertThat(designationRepository.findCurrent().get().getUser().getEmailAddress()).isEqualTo("chopper.tonytony@onepiece.com");
		assertThat(assignmentRepository.findById(AssignmentId.builder().userId(1l).poolId(3l).build())).isNotPresent();
		DesignationResponse response = DesignationResponse.builder()
				.response("accept")
				.emailAddress("chopper.tonytony@onepiece.com")
				.token("Vqiy3c3Z4uGm7rk8SECupeNKoOHeZ7")
				.build();
		Designation designation = service.processDesignationResponse(response);
		assertThat(designation.getUser().getEmailAddress()).isEqualTo("chopper.tonytony@onepiece.com");
		verify(notificationService, times(1)).emitAssignmentEvent(any(Assignment.class));
		assertThat(designationRepository.findCurrent()).isNotPresent();
		assertThat(assignmentRepository.findById(AssignmentId.builder().userId(1l).poolId(3l).build())).isPresent();
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
		verify(notificationService, times(1)).emitDesignationDeclinationEvent(any(Designation.class));
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
			.map(u -> assignmentRepository.save(createAssignment(u))).collect(Collectors.toList()); // <--- Create assignment for all users (lead already has an assignment)
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