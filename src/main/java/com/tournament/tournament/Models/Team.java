package com.tournament.tournament.Models;

import java.time.Instant;
import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "teams")
public class Team {

  @Id private String id;

  @Indexed(unique = true, sparse = true)
  private String name;

  @NotNull @NotEmpty private List<String> members;

  // TODO replace with user class
  @NotNull @NotBlank private String contact;

  @NotNull
  @Min(0)
  @Indexed
  private Float elo = 1000.0F;

  private Instant nextMatchDate;
}
