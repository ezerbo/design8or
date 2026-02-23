package com.ss.design8or.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ss.design8or.model.enums.DesignationStatus;
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
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"assignments"})
@EqualsAndHashCode(exclude = {"assignments"})
@Table(name = "pool")
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
	@OneToMany(mappedBy = "pool", orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Assignment> assignments;

	public boolean isActive() {
		return endDate == null;
	}

	@JsonProperty("participantCount")
	public int getParticipantCount() {
		if (assignments == null) return 0;
		return (int) assignments.stream()
			.filter(a -> a.getDesignationStatus() == DesignationStatus.ACCEPTED)
			.count();
	}

	@PrePersist
	public void onCreate() {
		startDate = new Date();
	}
}
