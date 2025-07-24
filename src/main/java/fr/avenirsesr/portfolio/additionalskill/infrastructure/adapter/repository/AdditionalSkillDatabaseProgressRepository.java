package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.AdditionalSkillCache;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillProgressRepository;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper.AdditionalSkillProgressMapper;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillProgressEntity;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.specification.AdditionalSkillProgressSpecification;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class AdditionalSkillDatabaseProgressRepository
    extends GenericJpaRepositoryAdapter<AdditionalSkillProgress, AdditionalSkillProgressEntity>
    implements AdditionalSkillProgressRepository {
  private final AdditionalSkillProgressJpaRepository jpaRepository;
  private final AdditionalSkillCache additionalSkillCache;

  public AdditionalSkillDatabaseProgressRepository(
      AdditionalSkillProgressJpaRepository jpaRepository,
      AdditionalSkillCache additionalSkillCache) {
    super(
        jpaRepository,
        jpaRepository,
        AdditionalSkillProgressMapper::fromDomain,
        AdditionalSkillProgressMapper::toDomain);
    this.jpaRepository = jpaRepository;
    this.additionalSkillCache = additionalSkillCache;
  }

  public void saveAllEntities(List<AdditionalSkillProgressEntity> entities) {
    if (entities != null && !entities.isEmpty()) {
      jpaRepository.saveAll(entities);
    }
  }

  private AdditionalSkill getAdditionalSkillById(UUID additionalSkillId) {
    return additionalSkillCache.findById(additionalSkillId);
  }

  @Override
  public boolean additionalSkillProgressAlreadyExists(
      AdditionalSkillProgress additionalSkillProgress) {
    return jpaRepository.exists(
        AdditionalSkillProgressSpecification.additionalSkillProgressAlreadyExists(
            additionalSkillProgress.getSkill().getId(),
            additionalSkillProgress.getStudent().getId()));
  }
}
