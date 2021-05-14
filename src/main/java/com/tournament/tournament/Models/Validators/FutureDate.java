package com.tournament.tournament.Models.Validators;

import java.lang.annotation.*;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = FutureDateValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FutureDate {
  String message() default "Matches must be scheduled more than 5 minutes in advance";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
