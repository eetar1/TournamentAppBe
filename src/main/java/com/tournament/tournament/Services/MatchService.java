package com.tournament.tournament.Services;

import com.tournament.tournament.Exceptions.AccessDeniedException;
import com.tournament.tournament.Exceptions.BadRequestException;
import com.tournament.tournament.Exceptions.EntityMissingException;
import com.tournament.tournament.Models.DTOs.CompleteMatchDTO;
import com.tournament.tournament.Models.DTOs.ScheduleDTO;
import com.tournament.tournament.Models.Match;
import com.tournament.tournament.Repositories.MatchRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MatchService {
  private final MatchRepository matchRepository;
  private final TeamService teamService;
  private final UserService userService;

  private final TournamentService tournamentService;

  public MatchService(
      MatchRepository matchRepository,
      TeamService teamService,
      UserService userService,
      @Lazy TournamentService tournamentService) {
    this.matchRepository = matchRepository;
    this.teamService = teamService;
    this.userService = userService;
    this.tournamentService = tournamentService;
  }

  public Match save(Match m) {
    return matchRepository.save(m);
  }

  public Page<Match> getByGame(String gameName, Pageable pageable) {
    return matchRepository.findByGameName(gameName, pageable);
  }

  public Page<Match> getByTeam(String teamName, Pageable pageable) {
    return matchRepository.findByHomeTeamOrAwayTeam(teamName, teamName, pageable);
  }

  public Match create(Match match) throws BadRequestException {
    try {
      //             Check official exist
      try {
        userService.findByUsername(match.getOfficial());
      } catch (EntityMissingException ex) {
        throw new BadRequestException("The official must have an account");
      }

      //            Check both teams exist
      try {
        teamService.getByName(match.getHomeTeam());
        teamService.getByName(match.getAwayTeam());
      } catch (EntityMissingException ex) {
        throw new BadRequestException("Teams must exist before they are added to a match");
      }

      match.setScore(null);

      if (match.getStatus() != Match.Match_Status.Created
          && match.getStatus() != Match.Match_Status.Scheduled) {
        throw new BadRequestException("Matches must be created with status as schedule or created");
      }

      if (match.getMatchDate() == null && match.getStatus().equals(Match.Match_Status.Scheduled)) {
        throw new BadRequestException("Scheduled matches must have a date");
      }

      return matchRepository.save(match);
    } catch (DuplicateKeyException ex) {
      throw new BadRequestException("A tournament with this name already exists");
    }
  }

  public Page<Match> getRecentDone(Pageable pageable) {
    return matchRepository.findAllByStatusOrderByMatchDateDesc(
        pageable, Match.Match_Status.Complete);
  }

  public Match scheduleMatch(String userName, ScheduleDTO schedule, String matchId)
      throws AccessDeniedException {
    Match storedMatch = matchRepository.findById(matchId).orElseThrow(EntityMissingException::new);

    if (!storedMatch.getOfficial().equals(userName)) {
      throw new AccessDeniedException("Only the match official can update its information");
    }

    storedMatch.setMatchDate(schedule.getDate());
    storedMatch.setStatus(Match.Match_Status.Scheduled);

    return matchRepository.save(storedMatch);
  }

  public Page<Match> getUpcoming(Pageable pageable) {
    return matchRepository.findAllByStatusOrderByMatchDateDesc(
        pageable, Match.Match_Status.Scheduled);
  }

  public Match completeMatch(CompleteMatchDTO completeMatch, String userName)
      throws AccessDeniedException {
    Match storedMatch =
        matchRepository.findById(completeMatch.getId()).orElseThrow(EntityMissingException::new);

    if (!storedMatch.getOfficial().equals(userName)) {
      throw new AccessDeniedException("Only the official can complete a match");
    }

    //    Update the results
    storedMatch.setScore(completeMatch.getScore());
    storedMatch.setResult(completeMatch.getResult());
    storedMatch.setStatus(Match.Match_Status.Complete);

    if (storedMatch.getTournamentId() != null) {
      tournamentService.completeMatch(storedMatch);
    }

    return matchRepository.save(storedMatch);
  }
}
