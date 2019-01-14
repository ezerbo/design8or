package com.ss.design8or.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ss.design8or.model.Designation;

/**
 * @author ezerbo
 *
 */
public interface DesignationRepository extends JpaRepository<Designation, Long> {

	Optional<Designation> findByCurrentTrue();
	
	@Query("from Designation d where d.current is true and d.status='DECLINED'")
	Designation findCurrentAndDeclined();
}