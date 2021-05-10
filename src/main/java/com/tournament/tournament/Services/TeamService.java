package com.tournament.tournament.Services;

import com.tournament.tournament.Exceptions.BadRequestException;
import com.tournament.tournament.Exceptions.EntityMissingException;
import com.tournament.tournament.Models.Team;
import com.tournament.tournament.Repositories.TeamRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class TeamService {
  private final TeamRepository teamRepository;
  private final UserService userService;

  public TeamService(TeamRepository teamRepository, UserService userService) {
    this.teamRepository = teamRepository;
    this.userService = userService;
  }

  public Team getByName(String teaName) {
    return teamRepository.findByName(teaName).orElseThrow(EntityMissingException::new);
  }

  public Team createTeam(Team team) throws BadRequestException {
    try {
      userService.findByUsername(team.getContact());
      return teamRepository.save(team);
    } catch (DuplicateKeyException ex) {
      throw new BadRequestException("A team with this name already exists");
    } catch (EntityMissingException ex) {
      throw new BadRequestException("The contact must have an account");
    }
  }
}
