package fr.avenirsesr.portfolio.user.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.shared.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.user.domain.model.UserFileUpload;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserFileUploadRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserFileUploadMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserFileUploadEntity;
import org.springframework.stereotype.Component;

@Component
public class UserFileUploadDatabaseRepository
    extends GenericJpaRepositoryAdapter<UserFileUpload, UserFileUploadEntity>
    implements UserFileUploadRepository {
  public UserFileUploadDatabaseRepository(UserFileUploadJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        UserFileUploadMapper::fromDomain,
        UserFileUploadMapper::toDomain);
  }
}
