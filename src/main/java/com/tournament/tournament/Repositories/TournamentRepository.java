package com.tournament.tournament.Repositories;

import com.tournament.tournament.Models.Tournament;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TournamentRepository extends MongoRepository<Tournament, String> {}
