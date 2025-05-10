package com.ss.design8or.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ss.design8or.model.Subscription;

import java.util.Optional;

/**
 * @author ezerbo
 *
 */
public interface SubscriptionRepository extends JpaRepository<Subscription, Long>  {

	Optional<Subscription> findByEndpoint(String endpoint);
}