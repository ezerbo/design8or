package com.ss.design8or.repository;

import com.ss.design8or.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


/**
 * @author ezerbo
 *
 */
public interface UserRepository extends JpaRepository<User, Long> {
	
	Optional<User> findByLeadTrue();
	
	Optional<User> findByEmailAddress(String emailAddress);

	boolean existsByEmailAddress(String emailAddress);
}