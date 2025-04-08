package com.ss.design8or.rest.response;

import com.ss.design8or.rest.DesignationResource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PoolDTO {

    private long id;

    private long progress;

    private long participantsCount;

    private Date startDate;

    private Date endDate;

    private DesignationResource.UserDTO lead;
}