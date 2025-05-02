package com.ss.design8or.service;

import com.ss.design8or.model.Assignment;
import com.ss.design8or.model.Pool;
import com.ss.design8or.model.PoolStatus;
import com.ss.design8or.model.User;
import com.ss.design8or.repository.AssignmentRepository;
import com.ss.design8or.repository.PoolRepository;
import com.ss.design8or.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author ezerbo
 *
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class AssignmentServiceTests {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PoolRepository poolRepository;
	
	@Autowired
	private AssignmentRepository repository;
	
	private AssignmentService service;
	
	@BeforeEach
	public void init() {
		service = new AssignmentService(userRepository, repository);
	}

	@Test
	public void createAssignmentSetsUserAsLead() {
		User oldLead = userRepository.findByLeadTrue().get();
		User user = userRepository.findById(3L).get();
		Pool currentPool = poolRepository.findOneByStatus(PoolStatus.STARTED).get();
		Assignment assignment = service.create(user, currentPool);
		User newLead = userRepository.findByLeadTrue().get();
		assertThat(newLead).isNotSameAs(oldLead);
		assertThat(newLead.getEmailAddress()).isEqualTo("zoro.roronoa@onpiece.com");
		assertThat(oldLead.getEmailAddress()).isEqualTo("luffy.monkey@onpiece.com");
		assertThat(assignment.getAssignmentDate()).isEqualToIgnoringSeconds(new Date());
		assertThat(assignment.getPool()).isSameAs(currentPool);
	}
}
