package com.ss.design8or.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ezerbo
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDTO {

	private String endpoint;
	
	private SubscriptionKey keys;
	
	public Subscription toSubscription() {
		return Subscription.builder()
				.endpoint(endpoint)
				.auth(keys.getAuth())
				.p256dh(keys.getP256dh())
				.build();
	}
}
