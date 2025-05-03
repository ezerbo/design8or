package com.ss.design8or.service;

import com.ss.design8or.error.exception.ResourceInUseException;
import com.ss.design8or.error.exception.ResourceNotFoundException;
import com.ss.design8or.model.User;
import com.ss.design8or.repository.UserRepository;
import com.ss.design8or.rest.request.UserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
	
	public User create(UserRequest request) {
		if (repository.existsByEmailAddress(request.getEmailAddress())) {
			log.error("Email address already in use");
			throw new ResourceInUseException("Email address already in use");
		}
		User user = User.builder()
				.emailAddress(request.getEmailAddress())
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.build();
		return repository.save(user);
	}

	public User update(UserRequest userRequest, Long id) {
		User user = findById(id);
		if (!userRequest.getEmailAddress().equalsIgnoreCase(user.getEmailAddress())
				&& repository.existsByEmailAddress(user.getEmailAddress())) {
			log.error("Email address '{}' already in use", user.getEmailAddress());
			throw new ResourceInUseException("Email address already in use");
		}
		user.setEmailAddress(userRequest.getEmailAddress());
		user.setFirstName(userRequest.getFirstName());
		user.setLastName(userRequest.getLastName());
		return repository.save(user);
	}

	
	public void delete(Long id) {
		User user = findById(id);
		repository.delete(user);
	}
	
	public List<User> findAll() {
		return repository.findAll();
	}
	
	public Optional<User> getCurrentLead() {
		return repository.findByLeadTrue();
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