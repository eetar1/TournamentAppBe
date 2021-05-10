package com.tournament.tournament.Repositories;

import com.tournament.tournament.Models.Match;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MatchRepository extends MongoRepository<Match, String> {

  Page<Match> findByGameName(String gameName, Pageable pageable);

  Page<Match> findByHomeTeamOrAwayTeam(String teamName, String team2Name, Pageable pageable);
}
