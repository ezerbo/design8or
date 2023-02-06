package com.ss.design8or.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ss.design8or.error.ParametersNotFoundException;
import com.ss.design8or.model.Parameter;
import com.ss.design8or.repository.ParameterRepository;

/**
 * @author ezerbo
 *
 */
@Service
@Transactional
public class ParameterService {

	private final ParameterRepository repository;
	
	public ParameterService(ParameterRepository repository) {
		this.repository = repository;
	}
	
	@Transactional(readOnly = true)
	public Parameter getParameter() {
		return repository.findAll()
				.stream()
				.findFirst().orElseThrow(ParametersNotFoundException::new);
	}
	
	public Parameter save(Parameter parameter) {
		return repository.save(parameter);
	}
}
