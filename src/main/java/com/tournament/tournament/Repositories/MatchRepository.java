package com.tournament.tournament.Repositories;

import com.tournament.tournament.Models.Match;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MatchRepository extends MongoRepository<Match, String> {}
