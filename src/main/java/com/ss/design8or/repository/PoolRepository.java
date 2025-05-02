package com.ss.design8or.repository;

import com.ss.design8or.model.Pool;
import com.ss.design8or.model.PoolStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * @author ezerbo
 *
 */
public interface PoolRepository extends JpaRepository<Pool, Long> {

	Optional<Pool> findOneByStatus(PoolStatus status);
	
	@Query("from Pool p where p.status = 'ENDED'")
	Page<Pool> findPast(Pageable page);

}