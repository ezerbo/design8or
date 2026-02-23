package com.ss.design8or.service;

import com.ss.design8or.error.exception.ResourceNotFoundException;
import com.ss.design8or.model.Configuration;
import com.ss.design8or.repository.ConfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ezerbo
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigurationService {

	private final ConfigurationRepository repository;

	public List<Configuration> findAll() {
		return repository.findAll();
	}

	public Configuration findById(Long id) {
		return repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Configuration with id " + id + " not found"));
	}

	public Configuration findByKey(String key) {
		return repository.findByKey(key)
				.orElseThrow(() -> new ResourceNotFoundException("Configuration with key " + key + " not found"));
	}

	public Configuration update(Long id, String value) {
		Configuration configuration = findById(id);
		configuration.setValue(value);
		return repository.save(configuration);
	}

	public String getValueByKey(String key) {
		return findByKey(key).getValue();
	}
}
