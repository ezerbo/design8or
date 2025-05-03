package com.ss.design8or.rest.request;


import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserRequest {

    private String firstName;

    private String lastName;

    @Email
    private String emailAddress;
}
