package com.tournament.tournament.Models;

import java.time.Instant;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "matches")
public class Match {

  public Match() {}

  public Match(String tournamentId, String gameName) {
    this.tournamentId = tournamentId;
    this.gameName = gameName;
    this.status = Match_Status.Planned;
  }

  @Id private String id;

  @Indexed private Instant matchDate;

  @NotNull @DBRef private Team homeTeam;

  @NotNull @DBRef private Team awayTeam;

  @NotNull
  @Length(min = 2)
  private String gameName;

  @NotNull private Match_Status status;

  @NotNull private Match_Result result = Match_Result.Pending;

  @NotNull @NotBlank private String official;

  private String score;

  private String tournamentId;

  public enum Match_Status {
    Created, // Object exists does not have schedule date
    Scheduled, // Waiting for play
    Complete,
    Cancelled,
    Planned // Used for tournaments matches without team info
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
