package com.tournament.tournament.Models.DTOs;

import com.tournament.tournament.Models.Match;
import java.time.Instant;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class MatchDTO {
  private String id;

  private Instant matchDate;

  @NotNull @NotBlank private String homeTeam;

  @NotBlank @NotNull private String awayTeam;

  @NotNull
  @Length(min = 2)
  private String gameName;

  private Match.Match_Status status;

  @NotNull @NotBlank private String official;
}
