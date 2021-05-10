package com.tournament.tournament.Models.Requests;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequest {

  @NotNull @NotBlank public String username;

  @NotNull @NotBlank public String password;
}
