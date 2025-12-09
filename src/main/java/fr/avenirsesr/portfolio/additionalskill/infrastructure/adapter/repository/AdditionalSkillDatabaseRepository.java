package fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository.AdditionalSkillRepository;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.mapper.AdditionalSkillMapper;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.model.AdditionalSkillEntity;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import java.util.Optional;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
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
  public Optional<AdditionalSkill> findByExternalSkillId(UUID externalSkillId) {
    return jpaRepository
        .findByExternalSkillId(externalSkillId)
        .map(AdditionalSkillMapper::toDomain);
  }

  @Override
  public AdditionalSkill saveOrGet(AdditionalSkill additionalSkill) {
    try {
      return save(additionalSkill);
    } catch (DataIntegrityViolationException e) {
      return findByExternalSkillId(additionalSkill.getExternalSkillId()).orElseThrow(() -> e);
    }
  }
}
