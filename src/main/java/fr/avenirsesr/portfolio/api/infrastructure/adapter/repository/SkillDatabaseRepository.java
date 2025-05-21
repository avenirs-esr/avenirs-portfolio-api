package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.Skill;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.SkillRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.SkillEntity;
import org.springframework.stereotype.Component;

@Component
public class SkillDatabaseRepository extends GenericJpaRepositoryAdapter<Skill, SkillEntity>
    implements SkillRepository {
  public SkillDatabaseRepository(SkillJpaRepository jpaRepository) {
    super(jpaRepository, SkillEntity::fromDomain);
  }
}
