package com.tournament.tournament.Services;

import com.tournament.tournament.Exceptions.BadRequestException;
import com.tournament.tournament.Exceptions.EntityMissingException;
import com.tournament.tournament.Models.Match;
import com.tournament.tournament.Models.Tournament;
import com.tournament.tournament.Repositories.TournamentRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TournamentService {

  private final TournamentRepository tournamentRepository;
  private final UserService userService;
  private final MatchService matchService;

  public TournamentService(
      TournamentRepository tournamentRepository,
      UserService userService,
      MatchService matchService) {
    this.tournamentRepository = tournamentRepository;
    this.userService = userService;
    this.matchService = matchService;
  }

  public Page<Tournament> getAll(Pageable pageable) {
    return tournamentRepository.findAll(pageable);
  }

  public Tournament getByName(String name) {
    return tournamentRepository.findByName(name).orElseThrow(EntityMissingException::new);
  }

  public Tournament create(Tournament tournament) throws BadRequestException {
    try {
      userService.findByUsername(tournament.getOrganizer());

      setUpMatchs(tournament);

      return tournamentRepository.save(tournament);
    } catch (DuplicateKeyException ex) {
      throw new BadRequestException("A tournament with this name already exists");
    }
  }

  private Tournament setUpMatchs(Tournament tournament) {
    double bottomBracket = Math.ceil(tournament.getTeams().size() / 2.0);
    boolean isBye = tournament.getTeams().size() % 2 != 0;

    List<String> shuffledTeams = new ArrayList<>(tournament.getTeams());
    Collections.shuffle(shuffledTeams);

    int idx = 0;

    if (isBye) {
      Match bye = new Match();
      bye.setHomeTeam(shuffledTeams.get(idx++));
      bye.setStatus(Match.Match_Status.Complete);
      bye.setResult(Match.Match_Result.Bye);
      bye.setGameName(tournament.getGameName());
      tournament.addMatch(bye);
    }

    for (; idx <= bottomBracket; idx += 2) {
      Match match = new Match();
      match.setHomeTeam(shuffledTeams.get(idx));
      match.setAwayTeam(shuffledTeams.get(idx + 1));
      match.setStatus(Match.Match_Status.Planned);
      match.setResult(Match.Match_Result.Pending);
      match.setGameName(tournament.getGameName());
      matchService.save(match);
      tournament.addMatch(match);
    }

    return tournament;
  }
}
