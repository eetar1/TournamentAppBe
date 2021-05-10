package com.tournament.tournament.Controllers;

import com.tournament.tournament.Exceptions.BadRequestException;
import com.tournament.tournament.Models.Requests.LoginRequest;
import com.tournament.tournament.Models.User;
import com.tournament.tournament.Services.UserService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/sign-up")
  public User signUp(@Valid @RequestBody User user) throws BadRequestException {
    return this.userService.createUser(user);
  }

  // DO NOT REMOVE
  // Method stub for the swagger
  @PostMapping("/login")
  public String login(@Valid @RequestBody LoginRequest user) {
    return "";
  }
}
