package fr.avenirsesr.portfolio.program.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.program.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.program.domain.port.output.repository.TrainingPathRepository;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper.TrainingPathMapper;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.TrainingPathEntity;
import org.springframework.stereotype.Repository;

@Repository
public class TrainingPathDatabaseRepository
    extends GenericJpaRepositoryAdapter<TrainingPath, TrainingPathEntity>
    implements TrainingPathRepository {
  private final TrainingPathJpaRepository jpaRepository;

  public TrainingPathDatabaseRepository(TrainingPathJpaRepository jpaRepository) {
    super(
        jpaRepository, jpaRepository, TrainingPathMapper::fromDomain, TrainingPathMapper::toDomain);
    this.jpaRepository = jpaRepository;
  }
}
