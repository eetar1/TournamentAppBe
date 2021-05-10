package com.tournament.tournament.Repositories;

import com.tournament.tournament.Models.Team;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TeamRepository extends MongoRepository<Team, String> {
  Optional<Team> findByName(String name);
}
