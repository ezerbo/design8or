package com.ss.design8or.model;

import java.util.List;

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
public class Pools {
	
	private Pool current;
	
	private List<Pool> past;
	
	private Long currentPoolProgress;
	
	private Long currentPoolParticipantsCount;

}
