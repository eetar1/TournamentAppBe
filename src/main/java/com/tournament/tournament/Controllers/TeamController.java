package com.tournament.tournament.Controllers;

import com.tournament.tournament.Exceptions.BadRequestException;
import com.tournament.tournament.Models.Team;
import com.tournament.tournament.Services.TeamService;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teams")
public class TeamController {

  private final TeamService teamService;

  public TeamController(TeamService teamService) {
    this.teamService = teamService;
  }

  @GetMapping("/{teamName}")
  public Team getTeamByName(@PathVariable("teamName") String teaName) {
    return teamService.getByName(teaName);
  }

  @PostMapping
  public Team createTeam(@Valid @RequestBody Team newTeam) throws BadRequestException {
    return teamService.createTeam(newTeam);
  }

  @GetMapping("/all")
  public Page<Team> getAllTeams(Pageable pageable) {
    return teamService.getAll(pageable);
  }
}
