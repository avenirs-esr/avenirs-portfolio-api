package fr.avenirsesr.portfolio.user.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.shared.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.user.domain.model.UserUpload;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserUploadRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserUploadMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserUploadEntity;
import org.springframework.stereotype.Component;

@Component
public class UserUploadDatabaseRepository
    extends GenericJpaRepositoryAdapter<UserUpload, UserUploadEntity>
    implements UserUploadRepository {
  public UserUploadDatabaseRepository(UserUploadJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, UserUploadMapper::fromDomain, UserUploadMapper::toDomain);
  }
}
