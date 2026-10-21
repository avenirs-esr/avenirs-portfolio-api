package fr.avenirsesr.portfolio.skill.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillReferentialType;
import fr.avenirsesr.portfolio.skill.domain.model.SkillReferential;
import fr.avenirsesr.portfolio.skill.domain.port.output.repository.SkillReferentialRepository;
import fr.avenirsesr.portfolio.skill.infrastructure.adapter.mapper.SkillReferentialMapper;
import fr.avenirsesr.portfolio.skill.infrastructure.adapter.model.SkillReferentialEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class SkillReferentialDatabaseRepository
    extends GenericJpaRepositoryAdapter<SkillReferential, SkillReferentialEntity>
    implements SkillReferentialRepository {
  private final SkillReferentialJpaRepository jpaRepository;

  public SkillReferentialDatabaseRepository(SkillReferentialJpaRepository jpaRepository) {
    super(
        jpaRepository,
        jpaRepository,
        SkillReferentialEntity.class,
        SkillReferentialMapper.INSTANCE);
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Optional<SkillReferential> findNationalByType(ESkillReferentialType type) {
    return jpaRepository
        .findByTypeAndInstitutionIdIsNull(type)
        .map(SkillReferentialMapper.INSTANCE::toDomain);
  }

  @Override
  public List<SkillReferential> findAllNational() {
    return jpaRepository.findAllByInstitutionIdIsNull().stream()
        .map(SkillReferentialMapper.INSTANCE::toDomain)
        .toList();
  }

  @Override
  public List<SkillReferential> findAllByInstitutionId(UUID institutionId) {
    return jpaRepository.findAllByInstitutionId(institutionId).stream()
        .map(SkillReferentialMapper.INSTANCE::toDomain)
        .toList();
  }
}
