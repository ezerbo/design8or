package com.ss.design8or.service;

import com.ss.design8or.model.Assignment;
import com.ss.design8or.model.AssignmentId;
import com.ss.design8or.model.Pool;
import com.ss.design8or.model.User;
import com.ss.design8or.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author ezerbo
 *
 */
@Service
@RequiredArgsConstructor
public class AssignmentService {

	private final AssignmentRepository repository;
	
	public Assignment create(User user, Pool pool) {
		AssignmentId assignmentId = AssignmentId.builder()
				.poolId(pool.getId()).userId(user.getId()).build();
		Assignment assignment = Assignment.builder()
				.id(assignmentId).user(user).pool(pool).build();
		return repository.save(assignment);
	}

}