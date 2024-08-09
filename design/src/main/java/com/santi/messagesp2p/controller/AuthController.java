package com.santi.messagesp2p.controller;

import com.santi.messagesp2p.config.tokens.JwtTokenProvider;
import com.santi.messagesp2p.dto.AuthRequest;
import com.santi.messagesp2p.dto.AuthResponse;
import com.santi.messagesp2p.dto.UserLogUpDTO;
import com.santi.messagesp2p.model.User;
import com.santi.messagesp2p.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired
  private final UserService userService;
  @Autowired
  private final PasswordEncoder passwordEncoder;
  @Autowired
  private final JwtTokenProvider jwtTokenProvider;
  @Autowired
  private final AuthenticationManager authenticationManager;

  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

  public AuthController(
      UserService userService,
      PasswordEncoder passwordEncoder,
      JwtTokenProvider jwtTokenProvider,
      AuthenticationManager authenticationManager
  ) {
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
    this.jwtTokenProvider = jwtTokenProvider;
    this.authenticationManager = authenticationManager;
  }

  @PostMapping("/register")
  public ResponseEntity<String> registerUser(@Valid @RequestBody UserLogUpDTO userLogUpDTO) {
    // Check if user already exists
    if (userService.existsByUsername(userLogUpDTO.getUsername())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username is already taken!");
    }

    if (userService.existsByEmail(userLogUpDTO.getEmail())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is already taken!");
    }

    // Create new user
    User user = new User();
    user.setUsername(userLogUpDTO.getUsername());
    user.setEmail(userLogUpDTO.getEmail());
    user.setPassword(passwordEncoder.encode(userLogUpDTO.getPassword()));  // Encode password

    userService.saveUser(user);
    logger.info("Usuario {} creado exitosamente: {}", user.getId(), userLogUpDTO.getUsername());
    return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
  }

  @PostMapping("/login")
  public ResponseEntity<?> createAuthenticatedToken(@RequestBody AuthRequest authRequest) {
    User user;
    try {
      // Check if user exists. Identifier may be username or email.
      user = userService.loadUserByUsername(authRequest.getIdentifier());
      // Check if credentials are valid.
      authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(authRequest.getIdentifier(), authRequest.getPassword())
      );

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    final String jwt = jwtTokenProvider.generateToken(user);
    logger.info("Welcome {}", user.getUsername());
    return ResponseEntity.ok(
        new AuthResponse(jwt)
        .setId(user.getId())
        .setUsername(user.getUsername())
        .setEmail(user.getEmail())
    );
  }


}
