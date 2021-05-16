package com.tournament.tournament.Controllers;

import com.tournament.tournament.Exceptions.BadRequestException;
import com.tournament.tournament.Models.Tournament;
import com.tournament.tournament.Services.SecurityService;
import com.tournament.tournament.Services.TournamentService;
import io.swagger.v3.oas.annotations.Parameter;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tournaments")
public class TournamentController {

  private final TournamentService tournamentService;
  private final SecurityService securityService;

  public TournamentController(
      TournamentService tournamentService, SecurityService securityService) {
    this.tournamentService = tournamentService;
    this.securityService = securityService;
  }

  @GetMapping("/{tournamentName}")
  public Tournament getByName(@PathVariable("tournammentName") String name) {
    return tournamentService.getByName(name);
  }

  @GetMapping("/me")
  public Page<Tournament> getMyTournaments(
      Pageable pageable,
      @Parameter(hidden = true) @RequestHeader("authorization") String authorization) {
    String userName = securityService.getUserFromJwt(authorization);
    return tournamentService.getMyTournaments(userName, pageable);
  }

  @GetMapping("/all")
  public Page<Tournament> getAllTournaments(Pageable pageable) {
    return tournamentService.getAll(pageable);
  }

  @PostMapping
  public Tournament createTournament(@Valid @RequestBody Tournament tournament)
      throws BadRequestException {
    return tournamentService.create(tournament);
  }
}
