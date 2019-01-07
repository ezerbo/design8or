package com.ss.design8or.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ss.design8or.model.Parameter;

/**
 * @author ezerbo
 *
 */
public interface ParameterRepository extends JpaRepository<Parameter, Long> {

}