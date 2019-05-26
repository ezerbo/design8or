package com.ss.design8or.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.time.LocalTime;

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
@Table(name = "app_parameter", catalog = "desig8or_db")
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
