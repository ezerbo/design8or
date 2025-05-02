package com.ss.design8or.rest.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReassignmentResponse {

    private String firstName;

    private String lastName;

    private String emailAddress;

    private String message;
}
