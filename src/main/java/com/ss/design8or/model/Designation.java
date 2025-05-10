package com.ss.design8or.model;

import com.ss.design8or.model.enums.DesignationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author ezerbo
 *
 */
@Data
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "designation")
public class Designation {

	// TODO Manually created or triggered by the system?

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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "reassignment_date")
	private Date reassignmentDate;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@PrePersist
	public void onSave() {
		designationDate = new Date();
		status = DesignationStatus.PENDING;
	}
}