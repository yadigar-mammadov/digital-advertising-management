package com.digitaladvertisingmanagement.auth.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.digitaladvertisingmanagement.auth.domain.model.User;
import com.digitaladvertisingmanagement.auth.domain.model.UserStatus;
import org.junit.jupiter.api.Test;

public class UserTest {

  @Test
  void shouldCreateActiveUser() {
    String email = "user@example.com";
    String passwordHash = "hashed-password";

    User user = User.create(email, passwordHash);
    assertNull(user.getId());
    assertEquals(email, user.getEmail());
    assertEquals(passwordHash, user.getPasswordHash());
    assertEquals(UserStatus.ACTIVE, user.getStatus());
  }

  @Test
  void shouldThrowExceptionWhenEmailIsNull() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> User.create(null, "hashed-password"));

    assertEquals("Email is required.", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenEmailIsEmpty() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> User.create("", "hashed-password"));

    assertEquals("Email is required.", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenEmailIsBlank() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> User.create("   ", "hashed-password"));

    assertEquals("Email is required.", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenPasswordHashIsNull() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> User.create("user@example.com", null));

    assertEquals("Password hash is required.", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenPasswordHashIsEmpty() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> User.create("user@example.com", ""));

    assertEquals("Password hash is required.", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenPasswordHashIsBlank() {
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> User.create("user@example.com", "   "));

    assertEquals("Password hash is required.", exception.getMessage());
  }
}
