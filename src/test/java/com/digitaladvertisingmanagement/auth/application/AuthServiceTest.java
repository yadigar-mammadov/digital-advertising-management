package com.digitaladvertisingmanagement.auth.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.digitaladvertisingmanagement.auth.application.command.LoginCommand;
import com.digitaladvertisingmanagement.auth.application.command.RegisterCommand;
import com.digitaladvertisingmanagement.auth.application.exception.EmailAlreadyExistsException;
import com.digitaladvertisingmanagement.auth.application.exception.InvalidCredentialsException;
import com.digitaladvertisingmanagement.auth.application.result.AuthResult;
import com.digitaladvertisingmanagement.auth.domain.model.User;
import com.digitaladvertisingmanagement.auth.domain.repository.UserRepository;
import com.digitaladvertisingmanagement.auth.infrastructure.security.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private JwtService jwtService;

  @InjectMocks private AuthService authService;

  private final String email = "test@example.com";
  private final String password = "password123";
  private final String passwordHash = "hashed-password";
  private final String token = "jwt-token";

  @Test
  void registerShouldRegisterUserAndReturnToken() {
    User user = User.create(email, passwordHash);

    when(userRepository.existsByEmail(email)).thenReturn(false);
    when(passwordEncoder.encode(password)).thenReturn(passwordHash);
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(jwtService.generateToken(user)).thenReturn(token);

    RegisterCommand request = new RegisterCommand(email, password);

    AuthResult result = authService.register(request);

    assertEquals(email, result.email());
    assertEquals(token, result.token());

    verify(userRepository).existsByEmail(email);
    verify(passwordEncoder).encode(password);
    verify(userRepository).save(any(User.class));
    verify(jwtService).generateToken(user);
  }

  @Test
  void registerShouldThrowExceptionWhenEmailAlreadyExists() {
    when(userRepository.existsByEmail(email)).thenReturn(true);

    RegisterCommand command = new RegisterCommand(email, "password123");

    EmailAlreadyExistsException exception =
        assertThrows(EmailAlreadyExistsException.class, () -> authService.register(command));

    assertEquals("Email already exists.", exception.getMessage());

    verify(userRepository).existsByEmail(email);
    verifyNoInteractions(passwordEncoder);
    verifyNoInteractions(jwtService);
  }

  @Test
  void loginShouldReturnAuthResultWhenCredentialsAreValid() {
    User user = User.create(email, passwordHash);

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(password, passwordHash)).thenReturn(true);
    when(jwtService.generateToken(user)).thenReturn(token);

    LoginCommand command = new LoginCommand(email, password);
    AuthResult result = authService.login(command);

    assertEquals(email, result.email());
    assertEquals(token, result.token());

    verify(userRepository).findByEmail(email);
    verify(passwordEncoder).matches(password, passwordHash);
    verify(jwtService).generateToken(user);
  }

  @Test
  void loginShouldThrowExceptionWhenUserNotFound() {
    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    LoginCommand command = new LoginCommand(email, "password123");
    InvalidCredentialsException exception =
        assertThrows(InvalidCredentialsException.class, () -> authService.login(command));

    assertEquals("Email or password is incorrect.", exception.getMessage());

    verify(userRepository).findByEmail(email);
    verifyNoInteractions(passwordEncoder);
    verifyNoInteractions(jwtService);
  }

  @Test
  void loginShouldThrowExceptionWhenPasswordIsIncorrect() {
    User user = User.create(email, passwordHash);
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(password, passwordHash)).thenReturn(false);

    LoginCommand command = new LoginCommand(email, password);

    InvalidCredentialsException exception =
        assertThrows(InvalidCredentialsException.class, () -> authService.login(command));

    assertEquals("Email or password is incorrect.", exception.getMessage());

    verify(userRepository).findByEmail(email);
    verify(passwordEncoder).matches(password, passwordHash);
    verifyNoInteractions(jwtService);
  }
}
