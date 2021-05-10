package com.tournament.tournament.Controllers;

import com.tournament.tournament.Exceptions.BadRequestException;
import com.tournament.tournament.Models.Tournament;
import com.tournament.tournament.Services.TournamentService;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tournament")
public class TournamentController {

  private final TournamentService tournamentService;

  public TournamentController(TournamentService tournamentService) {
    this.tournamentService = tournamentService;
  }

  @GetMapping("/{tournamentName}")
  public Tournament getByName(@PathVariable("tournammentName") String name) {
    return tournamentService.getByName(name);
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
