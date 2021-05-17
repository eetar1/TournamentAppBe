package com.tournament.tournament.Models;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TeamMember {

  @NotNull @NotBlank private String name;

  @NotNull @NotBlank private String position;
}
