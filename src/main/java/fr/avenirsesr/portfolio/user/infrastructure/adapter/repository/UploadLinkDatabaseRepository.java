package fr.avenirsesr.portfolio.user.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.user.domain.model.FileUploadLink;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UploadLinkRepository;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.FileUploadLinkMapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.FileUploadLinkEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.FileUploadLinkId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public class UploadLinkDatabaseRepository implements UploadLinkRepository {

  protected final JpaRepository<FileUploadLinkEntity, FileUploadLinkId> jpaRepository;

  public UploadLinkDatabaseRepository(
      JpaRepository<FileUploadLinkEntity, FileUploadLinkId> jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  public void save(FileUploadLink fileUploadLink) {
    jpaRepository.save(FileUploadLinkMapper.fromDomain(fileUploadLink));
  }
}
