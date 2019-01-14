package com.ss.design8or.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
@Table(name = "assignment", catalog = "desig8or_db")
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