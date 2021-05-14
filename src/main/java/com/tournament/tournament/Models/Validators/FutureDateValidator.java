package com.tournament.tournament.Models.Validators;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FutureDateValidator implements ConstraintValidator<FutureDate, Instant> {

  @Override
  public void initialize(FutureDate date) {}

  @Override
  public boolean isValid(Instant value, ConstraintValidatorContext context) {
    //        Match must be schedule more than five minutes in advance
    return value.isAfter(Instant.now().plus(5, ChronoUnit.MINUTES));
  }
}
