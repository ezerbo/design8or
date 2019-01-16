package com.ss.design8or.service;

import org.springframework.stereotype.Service;

import com.ss.design8or.model.Assignment;
import com.ss.design8or.model.AssignmentId;
import com.ss.design8or.model.Pool;
import com.ss.design8or.model.User;
import com.ss.design8or.repository.AssignmentRepository;
import com.ss.design8or.repository.UserRepository;

/**
 * @author ezerbo
 *
 */
@Service
public class AssignmentService {

	private UserRepository userRepository;
	private AssignmentRepository repository;
	
	public AssignmentService(UserRepository userRepository, AssignmentRepository repository) {
		this.repository = repository;
		this.userRepository = userRepository;
	}
	
	public Assignment create(User user, Pool pool) {
		userRepository.findByLeadTrue().map(u -> userRepository.save(u.unelect()));
		userRepository.save(user.elect());
		AssignmentId assignmentId = AssignmentId.builder()
				.poolId(pool.getId()).userId(user.getId()).build();
		Assignment assignment = Assignment.builder()
				.id(assignmentId).user(user).pool(pool).build();
		return repository.save(assignment);
	}
	
}