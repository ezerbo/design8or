package com.ss.design8or.service;

import com.ss.design8or.model.Subscription;
import com.ss.design8or.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author ezerbo
 *
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

	private final SubscriptionRepository repository;

	public Page<Subscription> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}
}
