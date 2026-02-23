package com.ss.design8or.repository;

import com.ss.design8or.model.Assignment;
import com.ss.design8or.model.Pool;
import com.ss.design8or.model.enums.DesignationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @author ezerbo
 *
 */
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

	List<Assignment> findByPoolAndDesignationStatusIsNull(Pool pool);

	List<Assignment> findByPool(Pool pool);

	List<Assignment> findByPoolAndDesignationStatus(Pool pool, DesignationStatus status);

	Optional<Assignment> findFirstByPoolAndDesignationStatusOrderByRespondedAtDesc(Pool pool, DesignationStatus status);

	boolean existsByPoolAndDesignationStatus(Pool pool, DesignationStatus status);

	long countByPoolAndDesignationStatus(Pool pool, DesignationStatus status);

	@Modifying
	@Query("UPDATE Assignment a SET a.designationStatus = 'INVALIDATED', a.respondedAt = CURRENT_TIMESTAMP " +
	       "WHERE a.pool.id = :poolId AND a.designationStatus = 'PENDING'")
	void invalidatePendingDesignations(@Param("poolId") Long poolId);
}
