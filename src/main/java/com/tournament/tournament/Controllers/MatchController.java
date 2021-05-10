package com.tournament.tournament.Controllers;

import com.tournament.tournament.Exceptions.BadRequestException;
import com.tournament.tournament.Models.Match;
import com.tournament.tournament.Services.MatchService;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/matchs")
public class MatchController {

  private final MatchService matchService;

  public MatchController(MatchService matchService) {
    this.matchService = matchService;
  }

  @GetMapping("/{gameName}")
  public Page<Match> getByGame(@PathVariable("gameName") String gameName) {
    return matchService.getByGame(gameName);
  }

  @GetMapping("/{teamName}")
  public Page<Match> getByTeam(@PathVariable("teamName") String teamName) {
    return matchService.getByTeam(teamName);
  }

  @PostMapping
  public Match createMatch(@Valid @RequestBody Match match) throws BadRequestException {
    return matchService.create(match);
  }
}
