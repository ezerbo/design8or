package com.ss.design8or.model;

import com.ss.design8or.rest.response.PoolDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentPoolStats {

    private long count;

    private PoolDTO pool;

}