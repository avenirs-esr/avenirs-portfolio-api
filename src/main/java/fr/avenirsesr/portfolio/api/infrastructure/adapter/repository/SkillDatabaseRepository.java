package fr.avenirsesr.portfolio.api.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.api.domain.model.Skill;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.SkillRepository;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.ProgramProgressMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.mapper.SkillMapper;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.model.SkillEntity;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SkillDatabaseRepository extends GenericJpaRepositoryAdapter<Skill, SkillEntity>
    implements SkillRepository {
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
  }

  @Override
  public List<SkillEntity> findAll() {
    return jpaRepository.findAll();
  }
}
