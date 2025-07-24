package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillProgressRepository;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper.AdditionalSkillProgressMapper;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillProgressEntity;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AdditionalSkillDatabaseProgressRepository
    implements AdditionalSkillProgressRepository {
  private final AdditionalSkillProgressJpaRepository jpaRepository;

  public AdditionalSkillDatabaseProgressRepository(
      AdditionalSkillProgressJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  public void saveAllEntities(List<AdditionalSkillProgressEntity> entities) {
    if (entities != null && !entities.isEmpty()) {
      jpaRepository.saveAll(entities);
    }
  }

  @Override
  public void save(AdditionalSkillProgress additionalSkillProgress) {
    AdditionalSkillProgressEntity entity =
        AdditionalSkillProgressMapper.toEntity(additionalSkillProgress);
    jpaRepository.save(entity);
  }
}
