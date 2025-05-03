package com.ss.design8or.rest.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.ss.design8or.model.Pool;
import com.ss.design8or.model.User;
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
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"pools", "currentLead", "progress", "participantsCount"})
public class GetPoolsResponse {
	
	private List<Pool> pools;
	
	private Long progress;
	
	private Long participantsCount;

	private User currentLead;

}
