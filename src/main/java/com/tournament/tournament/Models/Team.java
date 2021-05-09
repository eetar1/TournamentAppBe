package com.tournament.tournament.Models;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "teams")
public class Team {

  @Id private String id;

  @NotNull private List<String> members;

  // TODO replace with user class
  @NotNull @NotBlank private String contact;
}
