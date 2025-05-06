package com.ss.design8or.controller.request;


import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRequest {

    private String firstName;

    private String lastName;

    @Email
    private String emailAddress;
}
