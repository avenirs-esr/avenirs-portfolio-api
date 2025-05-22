package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.ExternalUser;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.ExternalUserRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.ExternalUserMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ExternalUserEntity;
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
