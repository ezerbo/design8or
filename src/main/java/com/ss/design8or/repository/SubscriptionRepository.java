package com.ss.design8or.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ss.design8or.model.Subscription;

/**
 * @author ezerbo
 *
 */
public interface SubscriptionRepository extends JpaRepository<Subscription, Long>  {

	Optional<Subscription> findByEndpointOrAuthOrP256dh(String endpoint, String auth, String p256dh);
}