package com.digitaladvertisingmanagement.auth.domain.model;

public class User {

  private final Long id;
  private final String email;
  private final String passwordHash;
  private final UserStatus status;

  public User(Long id, String email, String passwordHash, UserStatus status) {
    this.id = id;
    this.email = email;
    this.passwordHash = passwordHash;
    this.status = status;
  }

  public Long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public UserStatus getStatus() {
    return status;
  }

  public static User create(String email, String passwordHash) {
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("Email is required.");
    }

    if (passwordHash == null || passwordHash.isBlank()) {
      throw new IllegalArgumentException("Password hash is required.");
    }

    return new User(null, email, passwordHash, UserStatus.ACTIVE);
  }
}
