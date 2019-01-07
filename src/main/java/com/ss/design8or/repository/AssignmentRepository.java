package com.ss.design8or.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ss.design8or.model.Assignment;
import com.ss.design8or.model.AssignmentId;

/**
 * @author ezerbo
 *
 */
public interface AssignmentRepository extends JpaRepository<Assignment, AssignmentId> {

}
