package com.tournament.tournament.Models.DTOs;

import com.tournament.tournament.Models.Match;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CompleteMatchDTO {

  @NotNull @NotBlank private String id;

  @NotBlank @NotNull private String score;

  @NotNull private Match.Match_Result result;
}
