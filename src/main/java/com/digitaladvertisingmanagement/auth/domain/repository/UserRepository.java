package com.digitaladvertisingmanagement.auth.domain.repository;

import com.digitaladvertisingmanagement.auth.domain.model.User;
import java.util.Optional;

public interface UserRepository {
  User save(User user);

  boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);
}
