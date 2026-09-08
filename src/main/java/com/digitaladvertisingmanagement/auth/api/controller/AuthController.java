package com.digitaladvertisingmanagement.auth.api.controller;

import com.digitaladvertisingmanagement.auth.api.dto.AuthResponse;
import com.digitaladvertisingmanagement.auth.api.dto.LoginRequest;
import com.digitaladvertisingmanagement.auth.api.dto.RegisterRequest;
import com.digitaladvertisingmanagement.auth.application.AuthService;
import com.digitaladvertisingmanagement.auth.application.command.LoginCommand;
import com.digitaladvertisingmanagement.auth.application.command.RegisterCommand;
import com.digitaladvertisingmanagement.auth.application.result.AuthResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
    AuthResult result =
        authService.register(new RegisterCommand(request.email(), request.password()));
    return new AuthResponse(result.userId(), result.email(), result.token());
  }

  @PostMapping("/login")
  public AuthResponse login(@Valid @RequestBody LoginRequest request) {
    AuthResult result = authService.login(new LoginCommand(request.email(), request.password()));
    return new AuthResponse(result.userId(), result.email(), result.token());
  }
}
