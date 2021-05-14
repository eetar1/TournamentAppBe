package com.tournament.tournament.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class AccessDeniedException extends Exception {
  public AccessDeniedException(String s) {
    super(s);
  }
}
