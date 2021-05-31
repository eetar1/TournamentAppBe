package com.tournament.tournament.Services;

import com.tournament.tournament.Exceptions.BadRequestException;
import com.tournament.tournament.Exceptions.EntityMissingException;
import com.tournament.tournament.Models.Match;
import com.tournament.tournament.Models.Team;
import com.tournament.tournament.Models.Tournament;
import com.tournament.tournament.Repositories.TournamentRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TournamentService {

  private final TournamentRepository tournamentRepository;
  private final UserService userService;
  private final MatchService matchService;
  private final TeamService teamService;

  public TournamentService(
      TournamentRepository tournamentRepository,
      UserService userService,
      MatchService matchService,
      TeamService teamService) {
    this.tournamentRepository = tournamentRepository;
    this.userService = userService;
    this.matchService = matchService;
    this.teamService = teamService;
  }

  public Page<Tournament> getAll(Pageable pageable) {
    return tournamentRepository.findAll(pageable);
  }

  public Tournament getByName(String name) {
    return tournamentRepository.findByName(name).orElseThrow(EntityMissingException::new);
  }

  public Tournament create(Tournament tournament) throws BadRequestException {
    try {
      String brokenTeamName = "";

      userService.findByUsername(tournament.getOrganizer());

      try {
        for (String team :
            tournament.getTeams().stream().map(Team::getName).collect(Collectors.toList())) {
          brokenTeamName = team;
          teamService.getByName(team);
        }
      } catch (Exception ex) {
        throw new BadRequestException(
            "Teams must exist before they are added to a tournament. "
                + brokenTeamName
                + " does not exist.");
      }

      //            Set the tournament to have an id
      tournament = tournamentRepository.save(tournament);

      setUpMatches(tournament);

      return tournamentRepository.save(tournament);
    } catch (DuplicateKeyException ex) {
      throw new BadRequestException("A tournament with this name already exists");
    }
  }

  private Tournament setUpMatches(Tournament tournament) {
    //        Clone to preserve ordering
    List<Team> shuffledTeams = new ArrayList<>(tournament.getTeams());

    //        Randomly chose the first bye
    Collections.shuffle(shuffledTeams);

    ArrayList<Match> games = new ArrayList<>();
    ArrayList<List<Match>> bracket = new ArrayList<>();

    //        Adds the correct number of matches and byes by level
    for (double i = Math.ceil(tournament.getTeams().size() / 2.0); i > 1; i = Math.ceil(i / 2.0)) {
      for (double x = 0; x < i; x++) {
        Match tmp = new Match(tournament, tournament.getGameName(), tournament.getOrganizer());
        matchService.save(tmp);
        games.add(tmp);
      }
      bracket.add(games);
      games = new ArrayList<>();
    }
    Match last = new Match(tournament, tournament.getGameName(), tournament.getOrganizer());
    matchService.save(last);
    games.add(last);
    //        All matches will have status planned
    bracket.add(games);

    // Set match teams for level 0
    boolean isBye = tournament.getTeams().size() % 2 != 0;
    int teamIdx = 0;
    for (Match match : bracket.get(0)) {
      match.setGameName(tournament.getGameName());
      match.setOfficial(tournament.getOrganizer());

      if (isBye) {
        match.setStatus(Match.Match_Status.Complete);
        match.setResult(Match.Match_Result.Bye);
        match.setHomeTeam(shuffledTeams.get(0));

        //        Move team up due to the bye
        bracket.get(1).get(0).setHomeTeam(match.getHomeTeam());
        teamIdx = 1;
        isBye = false;
        continue;
      }

      match.setHomeTeam(shuffledTeams.get(teamIdx));
      match.setAwayTeam(shuffledTeams.get(teamIdx + 1));
      match.setStatus(Match.Match_Status.Created);
      match.setResult(Match.Match_Result.Pending);
      match = matchService.save(match);

      teamIdx += 2;
    }

    tournament.setMatches(bracket);

    return tournament;
  }

  public Match completeMatch(Match storedMatch) {
    Tournament storedTournament =
        tournamentRepository
            .findByName(storedMatch.getTournamentName())
            .orElseThrow(EntityMissingException::new);

    String winnerName =
        storedMatch.getResult().equals(Match.Match_Result.Home_Victory)
            ? storedMatch.getHomeTeam().getName()
            : storedMatch.getAwayTeam().getName();

    int nextRoundNumber = storedTournament.getRound() + 1;
    int currentRoundNumber = nextRoundNumber - 1;

    for (Match match : storedTournament.getMatches().get(currentRoundNumber)) {
      if (storedMatch.getId().equals(match.getId())) {
        match.setStatus(storedMatch.getStatus());
        match.setScore(storedMatch.getScore());
        match.setResult(storedMatch.getResult());
        break;
      }
    }

    var currentRound =
        nextRoundNumber < storedTournament.getMatches().size()
            ? storedTournament.getMatches().get(nextRoundNumber)
            : new ArrayList<Match>();
    for (Match match : currentRound) {

      if (match.getHomeTeam() == null) {
        match.setHomeTeam(teamService.getByName(winnerName));
        matchService.save(match);
        break;
      } else if (match.getAwayTeam() == null) {
        match.setStatus(Match.Match_Status.Created);
        match.setAwayTeam(teamService.getByName(winnerName));
        matchService.save(match);
        break;
      }
    }

    //        If not the finals and level is full up the level
    if (currentRound.size() > 0
        && roundComplete(currentRound, storedTournament, winnerName, nextRoundNumber)) {
      storedTournament.setRound(nextRoundNumber);
    }

    // Last game complete set tournament victor
    if (storedMatch
        .getId()
        .equals(
            storedTournament
                .getMatches()
                .get(storedTournament.getMatches().size() - 1)
                .get(0)
                .getId())) {
      storedTournament.setVictor(teamService.getByName(winnerName));
      storedTournament.setStatus(Tournament.Tournament_Status.Complete);
    }

    if (!currentRound.isEmpty()) {
      storedTournament.getMatches().set(nextRoundNumber, currentRound);
    }

    //    Update next match date
    Match nextMatch = matchService.nextScheduledMatchInTournament(storedTournament.getName());

    storedTournament.setNextMatchDate(nextMatch != null ? nextMatch.getMatchDate() : null);

    tournamentRepository.save(storedTournament);
    return storedMatch;
  }

  private boolean roundComplete(
      List<Match> currentRound,
      Tournament storedTournament,
      String winnerName,
      int nextRoundNumber) {
    boolean noByeRoundComplete =
        currentRound.get(currentRound.size() - 1).getAwayTeam() != null
            && currentRound.get(currentRound.size() - 1).getAwayTeam().equals(winnerName)
            && nextRoundNumber != storedTournament.getMatches().size();

    if (noByeRoundComplete) {
      return true;
    }

    boolean byeRequiredRoundComplete =
        currentRound.size() / 2 != storedTournament.getMatches().get(nextRoundNumber).size()
            && currentRound.get(currentRound.size() - 1).getHomeTeam() != null;

    if (byeRequiredRoundComplete) {
      Match bye = currentRound.get(currentRound.size() - 1);
      bye.setResult(Match.Match_Result.Bye);
      bye.setStatus(Match.Match_Status.Complete);

      var nextRound = storedTournament.getMatches().get(nextRoundNumber);
      nextRound.get(0).setHomeTeam(teamService.getByName(winnerName));

      return true;
    }

    return false;
  }

  public Page<Tournament> getMyTournaments(String userName, Pageable pageable) {
    return tournamentRepository.findAllByOrganizer(userName, pageable);
  }

  public void updateNextMatch(Instant date, Tournament tournament) {
    if (tournament.getNextMatchDate() == null || tournament.getNextMatchDate().isAfter(date)) {
      tournament.setNextMatchDate(date);
      tournamentRepository.save(tournament);
    }
  }

  public Page<Tournament> getMyOngoingTournaments(String userName, Pageable pageable) {
    return tournamentRepository.findAllByOrganizerAndStatus(
        userName, Tournament.Tournament_Status.In_progress, pageable);
  }
}
