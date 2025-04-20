package com.ss.design8or.model;

import java.util.Date;
import java.util.Objects;

import jakarta.persistence.*;
import org.apache.commons.lang3.RandomStringUtils;

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
@Table(name = "designation", catalog = "design8or_db")
public class Designation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
	
	@Column(name = "token")
	private String token;

	public Designation accept() {
		setStatus(DesignationStatus.ACCEPTED);
		return this;
	}
	
	public Designation decline() {
		setStatus(DesignationStatus.DECLINED);
		return this;
	}
	
	public Designation stale() {
		setStatus(DesignationStatus.STALED);
		return this;
	}
	
	public boolean isPending() {
		return Objects.equals(status, DesignationStatus.PENDING);
	}
	
	public boolean isDeclined() {
		return Objects.equals(status, DesignationStatus.DECLINED);
	}
	
	public boolean isAccepted() {
		return Objects.equals(status, DesignationStatus.ACCEPTED);
	}
	
	public boolean isStale() {
		return Objects.equals(status, DesignationStatus.STALED);
	}
	
	public Designation token(String token) {
		setToken(token);
		return this;
	}
	
	public Designation reassign() {
		setStatus(DesignationStatus.REASSIGNED);
		setToken(null);
		return this;
	}

	@PrePersist
	public void onSave() {
		setDesignationDate(new Date());
		setStatus(DesignationStatus.PENDING);
		setToken(RandomStringUtils.randomAlphanumeric(30));
	}
}