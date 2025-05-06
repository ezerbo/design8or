package com.ss.design8or.service;

import com.ss.design8or.model.Assignment;
import com.ss.design8or.model.AssignmentId;
import com.ss.design8or.model.Pool;
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

import java.util.Optional;

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
		service = new AssignmentService(repository);
	}

	@Test
	public void createAssignment() {
		Optional<Pool> currentPoolOp = poolRepository.findById(3L);
		assertThat(currentPoolOp).isPresent();
		Pool currentPool = currentPoolOp.get();
		Optional<User> userOp = userRepository.findById(3L);
		assertThat(userOp).isPresent();
		User user = userOp.get();
		AssignmentId assignmentId = AssignmentId.builder()
				.userId(user.getId())
				.poolId(currentPool.getId())
				.build();
		Optional<Assignment> assignmentOptional = repository.findById(assignmentId);
		assertThat(assignmentOptional).isEmpty();
		Assignment assignment = service.create(user, currentPool);
		assertThat(repository.findById(assignmentId)).isPresent();
		assertThat(assignment.getAssignmentDate()).isToday();
	}
}
