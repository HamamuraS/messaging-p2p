package com.santi.messagesp2p.service;

import com.santi.messagesp2p.exception.BadRequestException;
import com.santi.messagesp2p.model.User;
import com.santi.messagesp2p.repository.UserRepository;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  @Autowired
  private UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public void saveUser(User user) {
    userRepository.save(user);
  }

  public Iterable<User> findAllUsers() {
    return userRepository.findAll();
  }

  public Set<User> findAllById(Set<Long> ids) {
    try {
      Set<User> users = new HashSet<>(userRepository.findAllById(ids));
      if (users.isEmpty()) {
        logger.warn("No users found with the provided ids: {}", ids);
      }
      return users;
    } catch (Exception e) {
      logger.error("Error occurred while finding users by ids: {}", ids, e);
      throw new RuntimeException("Failed to find users by ids", e);
    }
  }

  public User getUserFromContext() {
    UserDetails userData = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return this.findByUsername(userData.getUsername()).orElseThrow(
        () -> new BadRequestException("User from context not found")
    );
  }

  public Optional<User> findById(Long id) {
    return userRepository.findById(id);
  }

  public Optional<User> findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  public Optional<User> findByUsernameOrEmail(String username, String email) {
    return userRepository.findByUsernameOrEmail(username, email);
  }

  // Used by JwtAuthenticationFilter which needs a UserDetailsService.
  // NOTE: This class implements UserDetailsService and User implements UserDetails
  @Override
  public User loadUserByUsername(String identifier) throws UsernameNotFoundException {
    return this.findByUsernameOrEmail(identifier, identifier)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + identifier));
  }

  public boolean existsByUsername(String username) {
    return userRepository.existsByUsername(username);
  }

  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

}
