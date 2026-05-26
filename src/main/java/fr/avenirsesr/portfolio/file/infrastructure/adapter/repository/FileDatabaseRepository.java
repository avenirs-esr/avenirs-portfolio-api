package fr.avenirsesr.portfolio.file.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.file.domain.port.output.repository.FileRepository;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.mapper.FileMapper;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.FileEntity;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.specification.FileSpecification;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class FileDatabaseRepository extends GenericJpaRepositoryAdapter<File, FileEntity>
    implements FileRepository {
  public FileDatabaseRepository(FileJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, FileEntity.class, FileMapper.INSTANCE);
  }

  @Override
  public List<File> findAllByElement(UUID elementId) {
    return jpaSpecificationExecutor.findAll(FileSpecification.ofElement(elementId)).stream()
        .map(FileMapper.INSTANCE::toDomain)
        .toList();
  }
}
