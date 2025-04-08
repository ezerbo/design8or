package com.ss.design8or.rest.response;

import java.util.List;

import com.ss.design8or.model.Pool;
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
public class GetPoolsResponse {
	
	private Pool current;
	
	private List<Pool> past;
	
	private Long progress;
	
	private Long participantsCount;

}
