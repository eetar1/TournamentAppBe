package com.tournament.tournament.Controllers;

import com.tournament.tournament.Exceptions.BadRequestException;
import com.tournament.tournament.Exceptions.EntityMissingException;
import com.tournament.tournament.Models.DTOs.TournamentDTO;
import com.tournament.tournament.Models.Tournament;
import com.tournament.tournament.Services.SecurityService;
import com.tournament.tournament.Services.TournamentService;
import io.swagger.v3.oas.annotations.Parameter;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tournaments")
public class TournamentController {

  private final TournamentService tournamentService;
  private final SecurityService securityService;
  private final ModelMapper modelMapper;

  public TournamentController(
      TournamentService tournamentService,
      SecurityService securityService,
      ModelMapper modelMapper) {
    this.tournamentService = tournamentService;
    this.securityService = securityService;
    this.modelMapper = modelMapper;
  }

  @GetMapping("/name/{tournamentName}")
  public Tournament getByName(@PathVariable("tournamentName") String name) throws EntityMissingException {
    return tournamentService.getByName(name);
  }

  @GetMapping("/ongoing/me")
  public Page<Tournament> getMyOngoingTournament(
      Pageable pageable,
      @Parameter(hidden = true) @RequestHeader("authorization") String authorization) {
    String userName = securityService.getUserFromJwt(authorization);
    return tournamentService.getMyOngoingTournaments(userName, pageable);
  }

  @GetMapping("/me")
  public Page<Tournament> getMyTournaments(
      Pageable pageable,
      @Parameter(hidden = true) @RequestHeader("authorization") String authorization) {
    String userName = securityService.getUserFromJwt(authorization);
    return tournamentService.getMyTournaments(userName, pageable);
  }

  @GetMapping("/id/{tournamentId}")
  public Tournament getTournamentById(@PathVariable("tournamentId") String tournamentId) {
    return tournamentService.getById(tournamentId);
  }

  @GetMapping("/all")
  public Page<Tournament> getAllTournaments(Pageable pageable) {
    return tournamentService.getAll(pageable);
  }

  @PostMapping
  public Tournament createTournament(
      @Valid @RequestBody TournamentDTO tournamentDTO,
      @Parameter(hidden = true) @RequestHeader("authorization") String authorization)
      throws BadRequestException {
    Tournament tournament = modelMapper.map(tournamentDTO, Tournament.class);
    String userName = securityService.getUserFromJwt(authorization);
    tournament.setOrganizer(userName);
    return tournamentService.create(tournament);
  }
}
