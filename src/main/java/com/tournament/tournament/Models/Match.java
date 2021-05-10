package com.tournament.tournament.Models;

import java.time.Instant;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "matches")
public class Match {

  @Id private String id;

  @NotNull @Indexed private Instant matchDate;

  @NotNull @NotBlank private String homeTeam;

  @NotBlank @NotNull private String awayTeam;

  @NotNull
  @Length(min = 2)
  private String GameName;

  @NotNull private Match_Status status;

  @NotNull private Match_Result result = Match_Result.Pending;

  @NotNull @NotBlank private String official;

  private Float score = 0.0F;

  public enum Match_Status {
    Created,
    Scheduled,
    Complete,
    Planned,
    Cancelled
  }

  public enum Match_Result {
    Home_Victory,
    Away_Victory,
    Draw,
    Incomplete,
    Cancelled,
    Pending,
    Bye
  }
}
