package com.ss.design8or.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ss.design8or.model.User;


/**
 * @author ezerbo
 *
 */
public interface UserRepository extends JpaRepository<User, Long> {
	
	@Query("from User u where u.id not in (select a.id.userId from Assignment a inner join Pool p on a.id.poolId = p.id where p.endDate is null) order by u.lastName asc")
	List<User> getAssignmentCandidates();
	
	Optional<User> findByLeadTrue();
	
	Optional<User> findByEmailAddress(String emailAddress);
}