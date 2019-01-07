package com.ss.design8or.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ss.design8or.model.Pool;

/**
 * @author ezerbo
 *
 */
public interface PoolRepository extends JpaRepository<Pool, Long> {
	
	@Query("from Pool p where p.endDate is null")
	Optional<Pool> findCurrent();
	
	@Query("from Pool p where p.endDate is not null")
	Page<Pool> findPast(Pageable page);
}