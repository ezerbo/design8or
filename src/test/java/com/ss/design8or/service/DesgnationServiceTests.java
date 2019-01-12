package com.ss.design8or.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.ss.design8or.model.User;
import com.ss.design8or.repository.AssignmentRepository;
import com.ss.design8or.repository.PoolRepository;
import com.ss.design8or.repository.UserRepository;

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
	
	private DesignationService service;
	
	@Before
	public void init() {
//		service = new DesignationService(poolRepository, userRepository,
//				assignmentRepository, notificationService);
	}
	
	@Test
	public void designateChoosesNextCandidateAlphabetically() {
		User lead = service.designate();
		assertThat(lead.getEmailAddress()).isEqualTo("robin.nico@onpiece.com");
		Optional<User> leadOp = userRepository.findByLeadTrue();
		assertThat(leadOp).isPresent();
		assertThat(lead).isEqualTo(leadOp.get());
	}
	
	@Test
	public void designateCreatesNewPoolOnMissingCurrentPool() {
		poolRepository.findCurrent().map(pool -> poolRepository.save(pool.end()));// <--- complete current pool
		assertThat(poolRepository.findCurrent()).isNotPresent();
		service.designate();
		assertThat(poolRepository.findCurrent()).isPresent();
	}
	
	@Test
	public void designateChoosesFromEntireUserBase() {
		poolRepository.findCurrent().map(pool -> poolRepository.save(pool.end()));// <--- complete current pool
		User lead = service.designate();
		assertThat(lead.getEmailAddress()).isEqualTo("luffy.monkey@onpiece.com");
	}
}