package com.digitaladvertisingmanagement.auth.application;

import com.digitaladvertisingmanagement.auth.application.command.LoginCommand;
import com.digitaladvertisingmanagement.auth.application.command.RegisterCommand;
import com.digitaladvertisingmanagement.auth.application.exception.EmailAlreadyExistsException;
import com.digitaladvertisingmanagement.auth.application.exception.InvalidCredentialsException;
import com.digitaladvertisingmanagement.auth.application.result.AuthResult;
import com.digitaladvertisingmanagement.auth.domain.model.User;
import com.digitaladvertisingmanagement.auth.domain.repository.UserRepository;
import com.digitaladvertisingmanagement.auth.infrastructure.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthService(
      UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  public AuthResult register(RegisterCommand request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new EmailAlreadyExistsException();
    }
    String passwordHash = passwordEncoder.encode(request.password());

    User user = User.create(request.email(), passwordHash);

    User savedUser = userRepository.save(user);

    String token = jwtService.generateToken(savedUser);

    return new AuthResult(savedUser.getId(), savedUser.getEmail(), token);
  }

  public AuthResult login(LoginCommand request) {

    User user = authenticate(request.email(), request.password());

    String token = jwtService.generateToken(user);

    return new AuthResult(user.getId(), user.getEmail(), token);
  }

  private User authenticate(String email, String rawPassword) {
    User user = userRepository.findByEmail(email).orElseThrow(this::invalidCredentials);

    if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
      throw invalidCredentials();
    }

    return user;
  }

  private InvalidCredentialsException invalidCredentials() {
    return new InvalidCredentialsException();
  }
}
