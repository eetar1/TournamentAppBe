package com.tournament.tournament.Models.Requests;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class LoginRequest {

    @NotNull @NotBlank
    public String username;

    @NotNull @NotBlank
    public String password;
}