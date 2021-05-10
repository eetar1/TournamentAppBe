package com.tournament.tournament.Services;

import com.tournament.tournament.Exceptions.BadRequestException;
import com.tournament.tournament.Exceptions.EntityMissingException;
import com.tournament.tournament.Models.Match;
import com.tournament.tournament.Repositories.MatchRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MatchService {
    private final MatchRepository matchRepository;
    private final TeamService teamService;
    private final UserService userService;

    public MatchService(
            MatchRepository matchRepository, TeamService teamService, UserService userService) {
        this.matchRepository = matchRepository;
        this.teamService = teamService;
        this.userService = userService;
    }

    public Match save(Match m) {
        return matchRepository.save(m);
    }

    public Page<Match> getByGame(String gameName, Pageable pageable) {
        return matchRepository.findByGameName(gameName, pageable);
    }

    public Page<Match> getByTeam(String teamName, Pageable pageable) {
        return matchRepository.findByHomeTeamOrAwayTeam(teamName, teamName, pageable);
    }

    public Match create(Match match) throws BadRequestException {
        try {
            //             Check official exist
            try {
                userService.findByUsername(match.getOfficial());
            } catch (EntityMissingException ex) {
                throw new BadRequestException("The official must have an account");
            }

            //            Check both teams exist
            try {
                teamService.getByName(match.getHomeTeam());
                teamService.getByName(match.getAwayTeam());
            } catch (EntityMissingException ex) {
                throw new BadRequestException("Teams must exist before they are added to a match");
            }

            return matchRepository.save(match);
        } catch (DuplicateKeyException ex) {
            throw new BadRequestException("A tournament with this name already exists");
        }
    }
}
