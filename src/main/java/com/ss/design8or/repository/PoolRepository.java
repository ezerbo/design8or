package com.ss.design8or.repository;

import com.ss.design8or.model.Pool;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author ezerbo
 *
 */
public interface PoolRepository extends JpaRepository<Pool, Long> {

	Page<Pool> findAllByOrderByStartDateDesc(Pageable pageable);
}
