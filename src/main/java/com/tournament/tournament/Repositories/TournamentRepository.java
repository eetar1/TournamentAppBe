package com.tournament.tournament.Repositories;

import com.tournament.tournament.Models.Tournament;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TournamentRepository extends MongoRepository<Tournament, String> {
  Optional<Tournament> findByName(String name);
}
