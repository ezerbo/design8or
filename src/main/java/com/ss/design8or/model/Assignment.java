package com.ss.design8or.model;

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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "assignment", catalog = "design8or_db")
public class Assignment {
	
	@EmbeddedId
	private AssignmentId id;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "assignment_date", nullable = false)
	private Date assignmentDate;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
	private User user;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "pool_id", nullable = false, insertable = false, updatable = false)
	private Pool pool;
	
	@PrePersist
	public void onSave() {
		setAssignmentDate(new Date());
	}
	
}