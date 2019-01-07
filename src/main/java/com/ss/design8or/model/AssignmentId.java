package com.ss.design8or.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ezerbo
 *
 */
@Data
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentId implements Serializable {
	
	private static final long serialVersionUID = -2124715621667674123L;
	
	@Column(name = "user_id", nullable = false)
	private Long userId;
	
	@Column(name = "pool_id", nullable = false)
	private Long poolId;
	
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof AssignmentId)) return false;
		AssignmentId castOther = (AssignmentId)other;
		return (this.poolId == castOther.poolId) && (this.userId == castOther.userId);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.poolId.intValue();
		hash = hash * prime + this.userId.intValue();
		return hash;
	}
}