package com.santi.messagesp2p.controller;

import com.santi.messagesp2p.dto.ChannelDTO;
import com.santi.messagesp2p.exception.BadRequestException;
import com.santi.messagesp2p.exception.NotFoundException;
import com.santi.messagesp2p.model.Channel;
import com.santi.messagesp2p.model.User;
import com.santi.messagesp2p.service.UserChannelService;
import com.santi.messagesp2p.service.UserService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  @Autowired
  private UserService userService;
  @Autowired
  private UserChannelService userChannelService;


  @GetMapping("/search")
  public ResponseEntity<User> searchUser(
      @RequestParam(required = false) String username,
      @RequestParam(required = false) String email
  ) {
    logger.info("Searching - username: {} - email: {}", username, email);
    if (username == null && email == null) {
      logger.warn("No search parameters provided");
      throw new BadRequestException("No search parameters provided");
    }
    Optional<User> optionalUser = userService.findByUsernameOrEmail(username, email);
    if (optionalUser.isEmpty()) {
      logger.warn("Couldn't find user with username: {} or email: {}", username, email);
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    logger.info("User found - username: {} - id: {}",
        optionalUser.get().getUsername(), optionalUser.get().getId());

    return new ResponseEntity<>(optionalUser.get(), HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getUserById(@PathVariable Long id) {
    logger.info("Required user {}", id);
    User user = userService.findById(id).orElseThrow(
        () -> new NotFoundException("User not found")
    );
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @GetMapping
  public ResponseEntity<Iterable<User>> getAllUsers() {
    logger.info("Obteniendo todos los usuarios");
    Iterable<User> users = userService.findAllUsers();
    return new ResponseEntity<>(users, HttpStatus.OK);
  }

  @GetMapping("/{userId}/channels")
  public ResponseEntity<?> getChannelsByUser(
      @PathVariable Long userId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size
  ) {
    logger.info("Requested channels for user {} with pagination, page: {}, size: {}", userId, page, size);
    Pageable pageable = PageRequest.of(page, size, Sort.by("channelId"));
    Page<Channel> channels = userChannelService.getChannelsByUser(userId, pageable);
    logger.info("Channels for user {} found", userId);
    Page<ChannelDTO> channelDTOS = channels.map(Channel::toDTO);
    return ResponseEntity.ok(channelDTOS);
  }
}