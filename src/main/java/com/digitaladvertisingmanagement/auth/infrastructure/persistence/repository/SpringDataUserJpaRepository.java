package com.digitaladvertisingmanagement.auth.infrastructure.persistence.repository;

import com.digitaladvertisingmanagement.auth.infrastructure.persistence.entity.UserJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataUserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
  boolean existsByEmail(String email);

  Optional<UserJpaEntity> findByEmail(String email);
}
