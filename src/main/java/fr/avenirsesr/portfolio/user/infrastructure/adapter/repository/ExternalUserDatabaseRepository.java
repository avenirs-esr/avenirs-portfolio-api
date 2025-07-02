package fr.avenirsesr.portfolio.user.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.shared.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.user.domain.model.ExternalUser;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.ExternalUserRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.ExternalUserMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.ExternalUserEntity;
import org.springframework.stereotype.Component;

@Component
public class ExternalUserDatabaseRepository
    extends GenericJpaRepositoryAdapter<ExternalUser, ExternalUserEntity>
    implements ExternalUserRepository {

  public ExternalUserDatabaseRepository(ExternalUserJpaRepository jpaRepository) {
    super(
        jpaRepository, jpaRepository, ExternalUserMapper::fromDomain, ExternalUserMapper::toDomain);
  }
}
