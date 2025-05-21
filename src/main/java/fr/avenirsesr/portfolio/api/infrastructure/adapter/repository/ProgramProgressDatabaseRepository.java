package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.ProgramProgressRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.ProgramProgressMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.ProgramProgressEntity;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.specification.ProgramProgressSpecifications;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ProgramProgressDatabaseRepository
    extends GenericJpaRepositoryAdapter<ProgramProgress, ProgramProgressEntity>
    implements ProgramProgressRepository {

  private final ProgramProgressJpaRepository jpaRepository;

  protected ProgramProgressDatabaseRepository(ProgramProgressJpaRepository jpaRepository) {
    super(jpaRepository, ProgramProgressMapper::fromModelToEntity);
    this.jpaRepository = jpaRepository;
  }

  @Override
  public List<ProgramProgress> getSkillsOverview(UUID userId) {
    return jpaRepository.findAll(ProgramProgressSpecifications.findByUserId(userId)).stream()
        .map(ProgramProgressMapper::fromEntityToModel)
        .toList();
  }
}
