package com.ss.design8or.repository;

import com.ss.design8or.model.Pool;
import com.ss.design8or.model.enums.PoolStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author ezerbo
 *
 */
public interface PoolRepository extends JpaRepository<Pool, Long> {

	Optional<Pool> findOneByStatus(PoolStatus status);

	List<Pool> findAllByOrderByStartDateDesc();

	Page<Pool> findAllByOrderByStartDateDesc(Pageable pageable);

	Long id(Long id);
}