package fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.repository;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.repository.GenericJpaRepositoryAdapter;
import fr.avenirsesr.portfolio.declaredskill.domain.model.DeclaredSkill;
import fr.avenirsesr.portfolio.declaredskill.domain.port.output.repository.DeclaredSkillRepository;
import fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.mapper.DeclaredSkillMapper;
import fr.avenirsesr.portfolio.declaredskill.infrastructure.adapter.model.DeclaredSkillEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
public class DeclaredSkillDatabaseRepository
    extends GenericJpaRepositoryAdapter<DeclaredSkill, DeclaredSkillEntity>
    implements DeclaredSkillRepository {
  private final DeclaredSkillJpaRepository jpaRepository;

  public DeclaredSkillDatabaseRepository(DeclaredSkillJpaRepository jpaRepository) {
    super(jpaRepository, jpaRepository, DeclaredSkillEntity.class, DeclaredSkillMapper.INSTANCE);
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Optional<DeclaredSkill> findById(UUID id) {
    return jpaRepository.findById(id).map(DeclaredSkillMapper.INSTANCE::toDomain);
  }

  @Override
  public DeclaredSkill saveOrGet(DeclaredSkill declaredSkill) {
    try {
      return save(declaredSkill);
    } catch (DataIntegrityViolationException e) {
      return findById(declaredSkill.getId()).orElseThrow(() -> e);
    }
  }
}
