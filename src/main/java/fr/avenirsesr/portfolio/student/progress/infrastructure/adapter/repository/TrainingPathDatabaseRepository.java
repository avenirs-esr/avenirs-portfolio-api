package fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.student.progress.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.mapper.TrainingPathMapper;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.model.TrainingPathEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import org.springframework.stereotype.Component;

@Component
public class TrainingPathDatabaseRepository
    extends GenericJpaRepositoryAdapter<TrainingPath, TrainingPathEntity> {
  private final TrainingPathJpaRepository jpaRepository;

  public TrainingPathDatabaseRepository(TrainingPathJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        TrainingPathMapper::fromDomain,
        TrainingPathMapper::toDomain);
    this.jpaRepository = jpaRepository;
  }
}
