package com.ss.design8or.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ss.design8or.error.EmailAddressInUseException;
import com.ss.design8or.error.UserNotFoundException;
import com.ss.design8or.model.User;
import com.ss.design8or.repository.UserRepository;

/**
 * @author ezerbo
 *
 */
@Service
public class UserService {

	private final UserRepository repository;
	
	public UserService(UserRepository repository) {
		this.repository = repository;
	}
	
	public User create(User user) {
		repository.findByEmailAddress(user.getEmailAddress())
		.ifPresent(e -> { throw new EmailAddressInUseException(e.getEmailAddress()); });
		return repository.save(user);
	}
	
	public User update(User user) {
		Optional<User> existingUserOp = repository.findByEmailAddress(user.getEmailAddress());
		if(existingUserOp.isPresent() && (!Objects.equals(user.getId(), existingUserOp.get().getId()))) {
			throw new EmailAddressInUseException(user.getEmailAddress());
		}
		User existingUser = repository.findById(user.getId())
				.map(u -> map(user, u))
				.orElseThrow(() -> new UserNotFoundException(user.getId()));
		return repository.save(existingUser);
	}
	
	public void delete(Long id) {
		User user = repository.findById(id)
				.orElseThrow(() -> new UserNotFoundException(id));
		repository.delete(user);
	}
	
	public List<User> getAll() {
		return repository.findAll();
	}
	
	public List<User> getCurrentPoolCandidates() {
		return repository.getCurrentPoolCandidates();
	}
	
	public Optional<User> getCurrentLead() {
		return repository.findByLeadTrue();
	}
	
	private User map(User from, User to) {
		return to.emailAddress(from.getEmailAddress())
				.firstName(from.getFirstName())
				.lastName(from.getLastName());
	}
}