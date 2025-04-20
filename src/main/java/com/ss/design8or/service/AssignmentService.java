package com.ss.design8or.service;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class AssignmentService {

	private final UserRepository userRepository;

	private final AssignmentRepository repository;
	
	public Assignment create(User user, Pool pool) {
		userRepository.findByLeadTrue().map(u -> userRepository.save(u.unElect()));
		userRepository.save(user.elect());
		AssignmentId assignmentId = AssignmentId.builder()
				.poolId(pool.getId()).userId(user.getId()).build();
		Assignment assignment = Assignment.builder()
				.id(assignmentId).user(user).pool(pool).build();
		return repository.save(assignment);
	}
	
}