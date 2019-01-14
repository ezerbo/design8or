package com.ss.design8or.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
@Table(name = "designation", catalog = "desig8or_db")
public class Designation {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private DesignationStatus status;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "designation_date", nullable = false)
	private Date designationDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "user_response_date")
	private Date userResponseDate;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	
	@Column(name = "is_current", nullable = false)
	private boolean current;
	
	public Designation status(DesignationStatus status) {
		setStatus(status);
		return this;
	}
	
	public Designation current(boolean current) {
		setCurrent(current);
		return this;
	}
	
	@PrePersist
	public void onSave() {
		setDesignationDate(new Date());
		setStatus(DesignationStatus.PENDING);
		setCurrent(true);
	}
}