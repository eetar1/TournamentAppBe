package com.tournament.tournament.Models.DTOs;

import com.tournament.tournament.Models.TeamMember;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TeamDTO {
  @NotNull @NotBlank private String name;

  @NotNull @NotEmpty private List<TeamMember> teamMembers;
}
