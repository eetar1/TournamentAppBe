package com.tournament.tournament.Repositories;

import com.tournament.tournament.Models.Team;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TeamRepository extends MongoRepository<Team, String> {
  Optional<Team> findByName(String name);

  Page<Team> findAllByOrderByEloDesc(Pageable pageable);

  Page<Team> findAllByContact(String userName, Pageable pageable);
}
