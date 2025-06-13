package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.Skill;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.SkillRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.ProgramProgressMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.SkillMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.SkillEntity;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SkillDatabaseRepository extends GenericJpaRepositoryAdapter<Skill, SkillEntity>
    implements SkillRepository {
  private final SkillJpaRepository jpaRepository;

  public SkillDatabaseRepository(SkillJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        skill ->
            SkillMapper.fromDomain(
                skill, ProgramProgressMapper.fromDomain(skill.getProgramProgress())),
        skill ->
            SkillMapper.toDomain(
                skill,
                ProgramProgressMapper.toDomain(skill.getProgramProgress(), ELanguage.FRENCH),
                ELanguage.FRENCH));
    this.jpaRepository = jpaRepository;
  }

  public void saveAllEntities(List<SkillEntity> entities) {
    if (entities != null && !entities.isEmpty()) {
      jpaRepository.saveAll(entities);
    }
  }

  @Override
  public List<SkillEntity> findAll() {
    return jpaRepository.findAll();
  }
}
