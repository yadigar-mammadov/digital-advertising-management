package com.digitaladvertisingmanagement.auth.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.digitaladvertisingmanagement.auth.application.security.AuthenticatedUser;
import com.digitaladvertisingmanagement.auth.domain.model.User;
import com.digitaladvertisingmanagement.auth.infrastructure.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JwtServiceTest {
  private static final String SECRET = "this-is-a-test-secret-key-that-is-long-enough-123456";

  private JwtService jwtService;

  @BeforeEach
  void setUp() {
    jwtService = new JwtService(SECRET, 60_000);
  }

  @Test
  void shouldGenerateValidJwtToken() {
    User user = createUserMock();

    String token = jwtService.generateToken(user);

    assertNotNull(token);
    assertTrue(jwtService.isValid(token));
  }

  @Test
  void shouldExtractAuthenticatedUser() {
    User user = createUserMock();

    String token = jwtService.generateToken(user);

    AuthenticatedUser authenticatedUser = jwtService.extractAuthenticatedUser(token);

    assertEquals(1L, authenticatedUser.id());
    assertEquals("user@example.com", authenticatedUser.email());
  }

  @Test
  void shouldReturnFalseForInvalidToken() {
    assertFalse(jwtService.isValid("invalid-token"));
  }

  @Test
  void shouldReturnFalseForExpiredToken() {
    JwtService expiredJwtService = new JwtService(SECRET, -1_000);

    User user = createUserMock();

    String token = expiredJwtService.generateToken(user);

    assertFalse(expiredJwtService.isValid(token));
  }

  private User createUserMock() {
    User user = mock(User.class);
    when(user.getId()).thenReturn(1L);
    when(user.getEmail()).thenReturn("user@example.com");
    return user;
  }
}
