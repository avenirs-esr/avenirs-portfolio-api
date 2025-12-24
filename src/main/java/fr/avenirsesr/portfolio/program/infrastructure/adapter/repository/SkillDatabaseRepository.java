package fr.avenirsesr.portfolio.program.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.program.domain.model.Skill;
import fr.avenirsesr.portfolio.program.domain.port.output.repository.SkillRepository;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.mapper.SkillMapper;
import fr.avenirsesr.portfolio.program.infrastructure.adapter.model.SkillEntity;
import org.springframework.stereotype.Component;

@Component
public class SkillDatabaseRepository extends GenericJpaRepositoryAdapter<Skill, SkillEntity>
    implements SkillRepository {
  private final SkillJpaRepository jpaRepository;

  public SkillDatabaseRepository(SkillJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, SkillEntity.class, SkillMapper.INSTANCE);
    this.jpaRepository = jpaRepository;
  }
}
