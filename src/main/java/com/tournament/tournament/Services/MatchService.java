package com.tournament.tournament.Services;

import com.tournament.tournament.Exceptions.AccessDeniedException;
import com.tournament.tournament.Exceptions.BadRequestException;
import com.tournament.tournament.Exceptions.EntityMissingException;
import com.tournament.tournament.Models.DTOs.CompleteMatchDTO;
import com.tournament.tournament.Models.DTOs.ScheduleDTO;
import com.tournament.tournament.Models.Match;
import com.tournament.tournament.Models.Team;
import com.tournament.tournament.Repositories.MatchRepository;
import java.time.Instant;
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
        teamService.getByName(match.getHomeTeam().getName());
        teamService.getByName(match.getAwayTeam().getName());
      } catch (EntityMissingException ex) {
        throw new BadRequestException("Teams must exist before they are added to a match");
      }

      if (match.getHomeTeam().equals(match.getAwayTeam())) {
        throw new BadRequestException("A team cannot play itself");
      }

      match.setScore(null);

      match.setStatus(
          match.getMatchDate() != null ? Match.Match_Status.Scheduled : Match.Match_Status.Created);

      if (match.getMatchDate() == null && match.getStatus().equals(Match.Match_Status.Scheduled)) {
        throw new BadRequestException("Scheduled matches must have a date");
      }

      if (match.getMatchDate() != null) {
        teamService.updateNextMatch(match.getMatchDate(), match);
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

    teamService.updateNextMatch(schedule.getDate(), storedMatch);

    if (storedMatch.getTournamentName() != null) {
      tournamentService.updateNextMatch(
          schedule.getDate(), tournamentService.getByName(storedMatch.getTournamentName()));
    }

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

    updateDates(storedMatch.getHomeTeam(), storedMatch.getAwayTeam());

    //    Update teams elos saves the team objcts
    updateElos(storedMatch.getHomeTeam(), storedMatch.getAwayTeam(), storedMatch.getResult());
    storedMatch = matchRepository.save(storedMatch);
    if (storedMatch.getTournamentName() != null) {
      tournamentService.completeMatch(storedMatch);
    }

    return storedMatch;
  }

  private void updateDates(Team homeTeam, Team awayTeam) {
    Match nextHomeMatch =
        matchRepository.findByHomeTeamOrAwayTeamAndStatusOrderByMatchDate(
            homeTeam.getName(), homeTeam.getName(), Match.Match_Status.Scheduled);
    Match nextAwayMatch =
        matchRepository.findByHomeTeamOrAwayTeamAndStatusOrderByMatchDate(
            homeTeam.getName(), homeTeam.getName(), Match.Match_Status.Scheduled);

    homeTeam.setNextMatchDate(nextHomeMatch != null ? nextAwayMatch.getMatchDate() : null);
    awayTeam.setNextMatchDate(nextAwayMatch != null ? nextAwayMatch.getMatchDate() : null);
  }

  private void updateElos(Team homeTeam, Team awayTeam, Match.Match_Result result) {

    //    The adjustment value
    int k = 32;

    double homePow = (awayTeam.getElo() - homeTeam.getElo()) / 400;
    double awayPow = (homeTeam.getElo() - awayTeam.getElo()) / 400;

    double homeExpected = Math.pow(10.0D, homePow) + 1.0;
    homeExpected = 1 / homeExpected;

    double awayExpected = Math.pow(10.0D, awayPow) + 1;
    awayExpected = 1 / awayExpected;

    if (result.equals(Match.Match_Result.Home_Victory)) {
      float newHomeElo = (float) (homeTeam.getElo() + k * (1.0 - homeExpected));
      homeTeam.setElo(newHomeElo);

      float newAwayElo = (float) (awayTeam.getElo() + k * (0.0 - awayExpected));
      awayTeam.setElo(newAwayElo);
    } else {
      float newHomeElo = (float) (homeTeam.getElo() + k * (0.0 - homeExpected));
      homeTeam.setElo(newHomeElo);

      float newAwayElo = (float) (awayTeam.getElo() + k * (1.0 - awayExpected));
      awayTeam.setElo(newAwayElo);
    }

    teamService.save(homeTeam);
    teamService.save(awayTeam);
  }

  public Page<Match> getMyToBeScheduled(String userName, Pageable pageable) {
    return matchRepository.findAllByOfficialAndStatus(
        userName, Match.Match_Status.Created, pageable);
  }

  public Page<Match> getMyToBeScored(String userName, Pageable pageable) {
    //    Get matches that are scheduled 1 day ago
    return matchRepository.findAllByOfficialAndStatusAndMatchDateBefore(
        userName, Match.Match_Status.Scheduled, Instant.now(), pageable);
  }

  public Match getById(String matchId) {
    return matchRepository.findById(matchId).orElseThrow(EntityMissingException::new);
  }

  public Page<Match> getMyMatches(String userName, Pageable pageable) {
    return matchRepository.findByStatusOrStatusAndOfficial(
        Match.Match_Status.Scheduled, Match.Match_Status.Created, userName, pageable);
  }

  public Match nextScheduledMatchInTournament(String tournamentName) {
    return matchRepository.findByTournamentNameAndStatusOrderByMatchDate(
        tournamentName, Match.Match_Status.Scheduled);
  }
}
