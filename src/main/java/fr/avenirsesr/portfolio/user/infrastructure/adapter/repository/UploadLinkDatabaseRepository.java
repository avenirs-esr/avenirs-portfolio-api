package fr.avenirsesr.portfolio.user.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.user.domain.model.UploadLink;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UploadLinkRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UploadLinkMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UploadLinkEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UploadLinkId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public class UploadLinkDatabaseRepository implements UploadLinkRepository {

  protected final JpaRepository<UploadLinkEntity, UploadLinkId> jpaRepository;

  public UploadLinkDatabaseRepository(JpaRepository<UploadLinkEntity, UploadLinkId> jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  public void save(UploadLink uploadLink) {
    jpaRepository.save(UploadLinkMapper.fromDomain(uploadLink));
  }
}
