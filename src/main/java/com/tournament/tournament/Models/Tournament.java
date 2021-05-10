package com.tournament.tournament.Models;

import java.time.Instant;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "tournaments")
public class Tournament {

  @Id private String id;

  @NotNull private List<Match> matches;

  @NotNull private List<Team> teams;

  @Indexed(unique = true)
  private String name;

  @NotNull private Instant nextMatchDate;

  // TODO replace with user class
  @NotNull @NotBlank private String organizer;
}
