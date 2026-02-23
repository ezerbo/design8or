package com.ss.design8or.repository;

import com.ss.design8or.model.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author ezerbo
 *
 */
public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {

	Optional<Configuration> findByKey(String key);
}
