package com.ss.design8or.model;

import static javax.persistence.GenerationType.IDENTITY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ezerbo
 *
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(catalog = "desig8or_db", name = "subscription")
public class Subscription {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@Column(name = "endpoint", nullable = false, unique = true)
	private String endpoint;

	@Column(name = "auth", nullable = false, unique = true)
	private String auth;

	@Column(name = "p256dh", nullable = false, unique = true)
	private String p256dh;
}
