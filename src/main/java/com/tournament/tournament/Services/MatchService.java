package com.tournament.tournament.Services;

import com.tournament.tournament.Models.Match;
import com.tournament.tournament.Repositories.MatchRepository;
import org.springframework.stereotype.Service;

@Service
public class MatchService {
  private final MatchRepository matchRepository;

  public MatchService(MatchRepository matchRepository) {
    this.matchRepository = matchRepository;
  }

  public Match save(Match m) {
    return matchRepository.save(m);
  }
}
