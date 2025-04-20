package com.ss.design8or.model;

import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * @author ezerbo
 *
 */
@Data
@jakarta.persistence.Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "app_parameter", catalog = "design8or_db")
public class Parameter {
	
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;
	
	@Column(name = "rotation_time", nullable = false)
	private LocalTime rotationTime;
	
	@Column(name = "st_req_timer_delay", nullable = false)
	private Long staleRequestTimeDelay;
}
