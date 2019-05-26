package com.ss.design8or.service;

import java.util.Optional;

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

	private ParameterRepository repository;
	
	public ParameterService(ParameterRepository repository) {
		this.repository = repository;
	}
	
	@Transactional(readOnly = true)
	public Parameter getParameter() {
		return repository.findAll()
				.stream()
				.findFirst().orElseThrow(() -> new ParametersNotFoundException());
	}
	
	@Transactional(readOnly = true)
	public Optional<Parameter> getOptionalParamter() {
		return repository.findAll()
				.stream()
				.findFirst();
	}
	
	public Parameter save(Parameter parameter) {
		return repository.save(parameter);
	}
}
