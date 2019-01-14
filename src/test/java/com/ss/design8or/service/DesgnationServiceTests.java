package com.ss.design8or.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import org.jasypt.util.text.BasicTextEncryptor;
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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.ss.design8or.TestConfig;
import com.ss.design8or.error.InvalidDesignationResponseException;
import com.ss.design8or.error.UserNotFoundException;
import com.ss.design8or.model.Assignment;
import com.ss.design8or.model.Designation;
import com.ss.design8or.model.DesignationResponse;
import com.ss.design8or.model.DesignationStatus;
import com.ss.design8or.model.Pool;
import com.ss.design8or.model.User;
import com.ss.design8or.repository.AssignmentRepository;
import com.ss.design8or.repository.DesignationRepository;
import com.ss.design8or.repository.PoolRepository;
import com.ss.design8or.repository.UserRepository;

@DataJpaTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
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

	@Autowired
	private BasicTextEncryptor basicTextEncryptor;

	private DesignationService service;
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void init() {
		service = new DesignationService(poolRepository, userRepository, designationRepository, notificationService,
				assignmentRepository, basicTextEncryptor);
	}

//	@Test
//	public void designateChoosesNextCandidateAlphabetically() {
//		User lead = service.designate();
//		assertThat(lead.getEmailAddress()).isEqualTo("robin.nico@onpiece.com");
//		Optional<User> leadOp = userRepository.findByLeadTrue();
//		assertThat(leadOp).isPresent();
//		assertThat(lead).isEqualTo(leadOp.get());
//	}
	
	@Test(expected = UserNotFoundException.class)
	public void designateThrowsUserNotFoundException() {
		service.designate(User.builder().id(0L).build());
	}
	
	@Test
	public void designateSetsCurrentDesignationStatusToAssigned() {
		Designation oldPendingDesignation = designationRepository.findByCurrentTrue().get();
		assertThat(oldPendingDesignation.getStatus()).isEqualByComparingTo(DesignationStatus.PENDING);
		assertTrue(oldPendingDesignation.isCurrent());
		User user = service.designate(User.builder().id(2L).build());
		Designation newPendingDesignation = designationRepository.findByCurrentTrue().get();
		assertThat(oldPendingDesignation.getStatus()).isEqualByComparingTo(DesignationStatus.REASSIGNED);
		assertFalse(oldPendingDesignation.isCurrent());
		assertThat(newPendingDesignation.getStatus()).isEqualByComparingTo(DesignationStatus.PENDING);
		assertThat(newPendingDesignation.isCurrent());
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
				.token("FywdMxiG+E30iQp6GPWrN6Ql2ECWZG+u")
				.build();
		service.processDesignationResponse(response);
	}
	
	@Test
	public void createAssignmentSetsUserAsLead() throws Exception {
		User oldLead = userRepository.findByLeadTrue().get();
		User user = userRepository.findById(3L).get();
		Method createAssignment = DesignationService.class.getDeclaredMethod("createAssignment", User.class);
		createAssignment.setAccessible(true);
		Assignment assignment = (Assignment) createAssignment.invoke(service, user);
		User newLead = userRepository.findByLeadTrue().get();
		
		assertThat(newLead).isNotSameAs(oldLead);
		assertThat(newLead.getEmailAddress()).isEqualTo("zoro.roronoa@onpiece.com");
		assertThat(oldLead.getEmailAddress()).isEqualTo("luffy.monkey@onpiece.com");
		
		assertThat(assignment.getAssignmentDate()).isEqualToIgnoringSeconds(new Date());
		assertThat(assignment.getPool()).isSameAs(getCurrentPool());
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

//	@Test
//	public void designateCreatesNewPoolOnMissingCurrentPool() {
//		poolRepository.findCurrent().map(pool -> poolRepository.save(pool.end()));// <--- complete current pool
//		assertThat(poolRepository.findCurrent()).isNotPresent();
//		service.designate();
//		assertThat(poolRepository.findCurrent()).isPresent();
//	}
//
//	@Test
//	public void designateChoosesFromEntireUserBase() {
//		poolRepository.findCurrent().map(pool -> poolRepository.save(pool.end()));// <--- complete current pool
//		User lead = service.designate();
//		assertThat(lead.getEmailAddress()).isEqualTo("luffy.monkey@onpiece.com");
//	}
}