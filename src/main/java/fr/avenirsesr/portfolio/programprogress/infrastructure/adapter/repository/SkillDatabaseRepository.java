package fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.programprogress.domain.model.Skill;
import fr.avenirsesr.portfolio.programprogress.domain.port.output.repository.SkillRepository;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.mapper.ProgramProgressMapper;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.mapper.SkillMapper;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.model.SkillEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
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
                skill, ProgramProgressMapper.toDomain(skill.getProgramProgress())));
    this.jpaRepository = jpaRepository;
  }

  public void saveAllEntities(List<SkillEntity> entities) {
    if (entities != null && !entities.isEmpty()) {
      jpaRepository.saveAll(entities);
    }
  }

  // TODO: remove this methode, is is not use and should not return an entity.
  @Override
  public List<SkillEntity> findAll() {
    return jpaRepository.findAll();
  }
}
