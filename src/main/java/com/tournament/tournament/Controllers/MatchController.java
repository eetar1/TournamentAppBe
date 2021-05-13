package com.tournament.tournament.Controllers;

import com.tournament.tournament.Exceptions.BadRequestException;
import com.tournament.tournament.Models.Match;
import com.tournament.tournament.Services.MatchService;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/matchs")
public class MatchController {

  private final MatchService matchService;

  public MatchController(MatchService matchService) {
    this.matchService = matchService;
  }

  @GetMapping("/{gameName}")
  public Page<Match> getByGame(@PathVariable("gameName") String gameName, Pageable pageable) {
    return matchService.getByGame(gameName, pageable);
  }

  @GetMapping("/{teamName}")
  public Page<Match> getByTeam(@PathVariable("teamName") String teamName, Pageable pageable) {
    return matchService.getByTeam(teamName, pageable);
  }

  @GetMapping("/recent/complete")
  public Page<Match> getRecentDoneMatches(Pageable pageable) {
    return matchService.getRecentDone(pageable);
  }

  @PostMapping
  public Match createMatch(@Valid @RequestBody Match match) throws BadRequestException {
    return matchService.create(match);
  }
}
