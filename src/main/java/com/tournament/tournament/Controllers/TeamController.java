package com.tournament.tournament.Controllers;

import com.tournament.tournament.Exceptions.BadRequestException;
import com.tournament.tournament.Models.Team;
import com.tournament.tournament.Services.SecurityService;
import com.tournament.tournament.Services.TeamService;
import io.swagger.v3.oas.annotations.Parameter;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teams")
public class TeamController {

  private final TeamService teamService;
  private final SecurityService securityService;

  public TeamController(TeamService teamService, SecurityService securityService) {
    this.teamService = teamService;
    this.securityService = securityService;
  }

  @GetMapping("/{teamName}")
  public Team getTeamByName(@PathVariable("teamName") String teaName) {
    return teamService.getByName(teaName);
  }

  @PostMapping
  public Team createTeam(@Valid @RequestBody Team newTeam) throws BadRequestException {
    return teamService.createTeam(newTeam);
  }

  @GetMapping("/me")
  public Page<Team> getByContact(
      Pageable pageable,
      @Parameter(hidden = true) @RequestHeader("authorization") String authorization) {
    String userName = securityService.getUserFromJwt(authorization);
    return teamService.getByContact(userName, pageable);
  }

  @GetMapping("/all")
  public Page<Team> getAllTeams(Pageable pageable) {
    return teamService.getAll(pageable);
  }

  @GetMapping("/top")
  public Page<Team> getTopTeams(Pageable pageable) {
    return teamService.getTop(pageable);
  }
}
