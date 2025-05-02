package com.ss.design8or.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @author ezerbo
 *
 */
@Data
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentId {
	
	@Column(name = "user_id", nullable = false)
	private Long userId;
	
	@Column(name = "pool_id", nullable = false)
	private Long poolId;
	
	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (!(other instanceof AssignmentId castOther)) return false;
        return (Objects.equals(this.poolId, castOther.poolId)) && (Objects.equals(this.userId, castOther.userId));
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