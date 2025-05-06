package com.ss.design8or.repository;

import com.ss.design8or.model.Designation;
import com.ss.design8or.model.enums.DesignationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author ezerbo
 *
 */
public interface DesignationRepository extends JpaRepository<Designation, Long> {

	Optional<Designation> findOneByStatus(DesignationStatus status);

	Optional<Designation> findOneByStatusNotIn(List<DesignationStatus> statuses);

	// find all and order by designation date descending & paginate
	Page<Designation> findAllByOrderByDesignationDateDesc(Pageable pageable);

}