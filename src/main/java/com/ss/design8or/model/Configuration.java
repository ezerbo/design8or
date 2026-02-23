package com.ss.design8or.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "configuration")
public class Configuration {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;

	@NotBlank
	@Column(name = "config_key", nullable = false, unique = true)
	private String key;

	@NotBlank
	@Column(name = "config_value", nullable = false)
	private String value;

	@Column(name = "description")
	private String description;
}
