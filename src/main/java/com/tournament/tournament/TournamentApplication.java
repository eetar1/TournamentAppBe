package com.tournament.tournament;

import com.tournament.tournament.Models.Team;
import com.tournament.tournament.Services.TeamService;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableMongoRepositories
public class TournamentApplication {

  public static void main(String[] args) {
    SpringApplication.run(TournamentApplication.class, args);
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public ModelMapper modelMapper(TeamService teamService) {
    ModelMapper modelMapper = new ModelMapper();
    Converter<String, Team> toTeam =
        new AbstractConverter<>() {
          protected Team convert(String source) {
            return teamService.getByName(source);
          }
        };

    modelMapper.addConverter(toTeam);

    return modelMapper;
  }
}
