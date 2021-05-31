package com.tournament.tournament.Models;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode
@Document(collection = "tournaments")
public class Tournament {

  @Id private String id;

  @NotNull private List<List<Match>> matches = new ArrayList<>();

  @NotNull
  @Size(min = 3)
  @DBRef
  private List<Team> teams = new ArrayList<>();

  @Indexed(unique = true)
  private String name;

  @NotNull @NotBlank @Indexed private String gameName;

  private Instant nextMatchDate;

  private Integer round = 0;

  private Team victor;

  private Tournament_Status status = Tournament_Status.In_progress;

  // TODO replace with user class
  @NotNull @NotBlank private String organizer;

  public enum Tournament_Status {
    In_progress,
    Complete
  }
}
