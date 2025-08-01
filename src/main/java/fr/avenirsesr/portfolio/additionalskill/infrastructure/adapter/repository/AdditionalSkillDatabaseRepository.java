package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillRepository;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper.AdditionalSkillMapper;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillEntity;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AdditionalSkillDatabaseRepository
    extends GenericJpaRepositoryAdapter<AdditionalSkill, AdditionalSkillEntity>
    implements AdditionalSkillRepository {
  private final AdditionalSkillJpaRepository jpaRepository;

  public AdditionalSkillDatabaseRepository(AdditionalSkillJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        AdditionalSkillMapper::fromDomain,
        AdditionalSkillMapper::toDomain);
    this.jpaRepository = jpaRepository;
  }

  @Override
  public List<AdditionalSkill> findByPathSegments_Skill_CodeIn(List<String> skillCodes) {
    return jpaRepository.findByPathSegments_Skill_CodeIn(skillCodes).stream()
        .map(AdditionalSkillMapper::toDomain)
        .toList();
  }
}
