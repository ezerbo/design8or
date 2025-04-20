package com.ss.design8or.model;

import java.util.Date;
import java.util.List;


import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author ezerbo
 *
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"assignments"})
@EqualsAndHashCode(exclude = {"assignments"})
@Table(name = "pool", catalog = "design8or_db")
public class Pool {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "start_date", nullable = false)
	private Date startDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "end_date")
	private Date endDate;
	
	@JsonIgnore
	@OneToMany(mappedBy = "pool", orphanRemoval = true)
	private List<Assignment> assignments;
	
	@PrePersist
	public void onCreate() {
		setStartDate(new Date());
	}
	
	public Pool end() {
		setEndDate(new Date());
		return this;
	}

	public boolean isClosed() {
		return endDate != null;
	}
}