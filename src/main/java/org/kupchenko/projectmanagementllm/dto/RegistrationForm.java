package org.kupchenko.projectmanagementllm.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class RegistrationForm {

    @NotBlank
    private String email;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String confirmPassword;
}

