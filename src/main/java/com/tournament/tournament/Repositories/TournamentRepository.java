package com.tournament.tournament.Repositories;

import com.tournament.tournament.Models.Tournament;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TournamentRepository extends MongoRepository<Tournament, String> {
  Optional<Tournament> findByName(String name);

  Page<Tournament> findAllByOrganizer(String userName, Pageable pageable);

  Page<Tournament> findAllByOrganizerAndStatus(
      String userName, Tournament.Tournament_Status in_progress, Pageable pageable);
}
