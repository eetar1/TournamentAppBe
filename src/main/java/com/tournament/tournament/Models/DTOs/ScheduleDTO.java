package com.tournament.tournament.Models.DTOs;

import com.tournament.tournament.Models.Validators.FutureDate;
import java.time.Instant;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScheduleDTO {
  @NotNull @FutureDate private Instant date;
}
