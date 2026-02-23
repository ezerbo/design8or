package com.ss.design8or.service;

import com.ss.design8or.error.exception.ResourceInUseException;
import com.ss.design8or.error.exception.ResourceNotFoundException;
import com.ss.design8or.model.Assignment;
import com.ss.design8or.model.Pool;
import com.ss.design8or.model.enums.DesignationStatus;
import com.ss.design8or.model.User;
import com.ss.design8or.repository.AssignmentRepository;
import com.ss.design8or.repository.UserRepository;
import com.ss.design8or.controller.request.UserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author ezerbo
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository repository;

	private final AssignmentRepository assignmentRepository;

	private final PoolService poolService;
	
	public User create(UserRequest request) {
		validateEmailAddress(request.getEmailAddress());
		User user = User.builder()
				.emailAddress(request.getEmailAddress())
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.build();
		return repository.save(user);
	}

	public User update(UserRequest userRequest, Long id) {
		User user = findById(id);
		if (!userRequest.getEmailAddress().equalsIgnoreCase(user.getEmailAddress())) {
			validateEmailAddress(userRequest.getEmailAddress());
		}
		user.setEmailAddress(userRequest.getEmailAddress());
		user.setFirstName(userRequest.getFirstName());
		user.setLastName(userRequest.getLastName());
		return repository.save(user);
	}

	private void validateEmailAddress(String emailAddress) {
		if (repository.findByEmailAddress(emailAddress).isPresent()) {
			log.error("Email address already in use");
			throw new ResourceInUseException("Email address already in use");
		}
	}

	public Optional<User> designatedUser() {
		Pool currentPool = poolService.getCurrent();
		return assignmentRepository.findFirstByPoolAndDesignationStatusOrderByRespondedAtDesc(
				currentPool, DesignationStatus.ACCEPTED)
				.map(Assignment::getUser);
	}
	
	public void delete(Long id) {
		User user = findById(id);
		repository.delete(user);
	}
	
	public Page<User> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}

	public Optional<User> getOne(Long id) {
		return repository.findById(id);
	}

	public User findById(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));
	}

	public User findByEmailAddress(String emailAddress) {
		return repository.findByEmailAddress(emailAddress)
				.orElseThrow(() -> new ResourceNotFoundException(emailAddress));
	}

}