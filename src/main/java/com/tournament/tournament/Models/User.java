package com.tournament.tournament.Models;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
public class User {

  @Id private String id;

  @NotNull
  @NotBlank
  @Indexed(unique = true)
  private String username;

  @Email @NotBlank private String email;

  @NotNull @NotBlank private String password;
}
