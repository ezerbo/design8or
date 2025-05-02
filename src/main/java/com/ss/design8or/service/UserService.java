package com.ss.design8or.service;

import com.ss.design8or.error.exception.EmailAddressInUseException;
import com.ss.design8or.error.exception.UserNotFoundException;
import com.ss.design8or.model.User;
import com.ss.design8or.repository.UserRepository;
import com.ss.design8or.rest.request.CreateUserRequest;
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
	
	public User create(CreateUserRequest request) {
		if (repository.existsByEmailAddress(request.getEmailAddress())) {
			log.error("Email address already in use");
			throw new EmailAddressInUseException(request.getEmailAddress());
		}
		User user = User.builder()
				.emailAddress(request.getEmailAddress())
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.build();
		return repository.save(user);
	}
	
	public User update(User user) {
		User existingUser = repository.findById(user.getId())
				.map(u -> {
					if (!u.getEmailAddress().equalsIgnoreCase(user.getEmailAddress())
							&& repository.existsByEmailAddress(user.getEmailAddress())) {
						log.error("Email address '{}' already in use", user.getEmailAddress());
						throw new EmailAddressInUseException(user.getEmailAddress());
					}
					return u.toBuilder().emailAddress(user.getEmailAddress())
							.firstName(user.getFirstName())
							.lastName(user.getLastName())
							.build();
				})
				.orElseThrow(() -> new UserNotFoundException(user.getId()));
		return repository.save(existingUser);
	}
	
	public void delete(Long id) {
		User user = repository.findById(id)
				.orElseThrow(() -> new UserNotFoundException(id));
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
				.orElseThrow(() -> new UserNotFoundException(id));
	}

	public long count() {
		return repository.count();
	}
}