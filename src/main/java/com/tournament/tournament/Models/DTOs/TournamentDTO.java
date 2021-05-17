package com.tournament.tournament.Models.DTOs;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class TournamentDTO {

  @NotNull
  @Size(min = 3)
  private List<String> teams = new ArrayList<>();

  private String name;

  @NotNull @NotBlank private String gameName;
}
