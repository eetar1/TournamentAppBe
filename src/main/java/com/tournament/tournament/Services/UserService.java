package com.tournament.tournament.Services;

import com.tournament.tournament.Exceptions.BadRequestException;
import com.tournament.tournament.Exceptions.EntityMissingException;
import com.tournament.tournament.Models.User;
import com.tournament.tournament.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final BCryptPasswordEncoder passwordEncoder;

  @Autowired
  public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public User createUser(User user) throws BadRequestException {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    try {
      User out = userRepository.save(user);
      out.setPassword(null);
      return out;
    } catch (Exception ex) {
      throw new BadRequestException("A user with this username already exist");
    }
  }
  // Required by spring for auth
  public User findByUsername(String username) {
    return userRepository.findByUsername(username).orElseThrow(EntityMissingException::new);
  }
}
