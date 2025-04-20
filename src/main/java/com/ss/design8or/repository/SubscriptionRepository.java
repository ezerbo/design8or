package com.ss.design8or.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ss.design8or.model.Subscription;

/**
 * @author ezerbo
 *
 */
public interface SubscriptionRepository extends JpaRepository<Subscription, Long>  {

	boolean existsByEndpointOrAuthOrP256dh(String endpoint, String auth, String p256dh);
}