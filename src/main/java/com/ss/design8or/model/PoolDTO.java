package com.ss.design8or.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PoolDTO {

    private Long id;

    private Date startDate;

    private Date endDate;

    private Optional<UserDTO> lead;
}