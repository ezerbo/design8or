package com.ss.design8or.service.notification;

import com.ss.design8or.model.Assignment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Assignments {

    private Assignment assignment;

    private Assignment deletedAssignment;
}
