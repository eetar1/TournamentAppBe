package com.tournament.tournament.Controllers;

import com.tournament.tournament.Exceptions.AccessDeniedException;
import com.tournament.tournament.Exceptions.BadRequestException;
import com.tournament.tournament.Models.DTOs.CompleteMatchDTO;
import com.tournament.tournament.Models.DTOs.MatchDTO;
import com.tournament.tournament.Models.DTOs.ScheduleDTO;
import com.tournament.tournament.Models.Match;
import com.tournament.tournament.Services.MatchService;
import com.tournament.tournament.Services.SecurityService;
import io.swagger.v3.oas.annotations.Parameter;
import javax.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/matches")
public class MatchController {

  private final MatchService matchService;
  private final ModelMapper modelMapper;
  private final SecurityService securityService;

  public MatchController(
      MatchService matchService, SecurityService securityService, ModelMapper modelMapper) {
    this.matchService = matchService;
    this.securityService = securityService;

    this.modelMapper = modelMapper;
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

  @PostMapping("/schedule/{matchId}")
  public Match scheduleMatch(
      @PathVariable String matchId,
      @Parameter(hidden = true) @RequestHeader("authorization") String authorization,
      @Valid @RequestBody ScheduleDTO schedule)
      throws AccessDeniedException {
    String userName = securityService.getUserFromJwt(authorization);
    return matchService.scheduleMatch(userName, schedule, matchId);
  }

  @GetMapping("/schedule/me")
  public Page<Match> getToBeScheduled(
      Pageable pageable,
      @Parameter(hidden = true) @RequestHeader("authorization") String authorization) {
    String userName = securityService.getUserFromJwt(authorization);
    return matchService.getMyToBeScheduled(userName, pageable);
  }

  @GetMapping("/score/me")
  public Page<Match> getToBeScored(
      Pageable pageable,
      @Parameter(hidden = true) @RequestHeader("authorization") String authorization) {
    String userName = securityService.getUserFromJwt(authorization);
    return matchService.getMyToBeScored(userName, pageable);
  }

  @GetMapping("/recent")
  public Page<Match> getUpcoming(Pageable pageable) {
    return matchService.getUpcoming(pageable);
  }

  @PostMapping("/complete")
  public Match completeMatch(
      @Valid @RequestBody CompleteMatchDTO completeMatch,
      @Parameter(hidden = true) @RequestHeader("authorization") String authorization)
      throws AccessDeniedException {
    String userName = securityService.getUserFromJwt(authorization);
    return matchService.completeMatch(completeMatch, userName);
  }

  @PostMapping
  public Match createMatch(@Valid @RequestBody MatchDTO matchDTO) throws BadRequestException {
    Match match = modelMapper.map(matchDTO, Match.class);
    return matchService.create(match);
  }
}
