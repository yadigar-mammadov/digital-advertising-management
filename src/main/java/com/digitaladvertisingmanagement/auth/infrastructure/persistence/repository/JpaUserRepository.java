package com.digitaladvertisingmanagement.auth.infrastructure.persistence.repository;

import com.digitaladvertisingmanagement.auth.domain.model.User;
import com.digitaladvertisingmanagement.auth.domain.repository.UserRepository;
import com.digitaladvertisingmanagement.auth.infrastructure.persistence.entity.UserJpaEntity;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
class JpaUserRepository implements UserRepository {

  private final SpringDataUserJpaRepository repository;

  JpaUserRepository(SpringDataUserJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public User save(User user) {
    UserJpaEntity saved = repository.save(toJpa(user));
    return toDomain(saved);
  }

  @Override
  public boolean existsByEmail(String email) {
    return repository.existsByEmail(email);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return repository.findByEmail(email).map(JpaUserRepository::toDomain);
  }

  private static UserJpaEntity toJpa(User user) {
    return new UserJpaEntity(
        user.getId(), user.getEmail(), user.getPasswordHash(), user.getStatus());
  }

  private static User toDomain(UserJpaEntity entity) {
    return new User(
        entity.getId(), entity.getEmail(), entity.getPasswordHash(), entity.getStatus());
  }
}
