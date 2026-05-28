package fr.avenirsesr.portfolio.user.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserPrincipalEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserPrincipalJpaRepository
    extends JpaRepository<UserPrincipalEntity, UUID>,
        JpaSpecificationExecutor<UserPrincipalEntity> {
  Optional<UserPrincipalEntity> findByUserId(UUID userId);

  Optional<UserPrincipalEntity> findByEppn(String eppn);
}
