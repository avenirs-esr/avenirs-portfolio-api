package fr.avenirsesr.portfolio.declaredskill.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.declaredskill.domain.model.DeclaredSkill;
import java.util.Optional;
import java.util.UUID;

public interface DeclaredSkillRepository extends GenericRepositoryPort<DeclaredSkill> {
  Optional<DeclaredSkill> findByExternalSkillId(UUID externalSkillId);

  DeclaredSkill saveOrGet(DeclaredSkill declaredSkill);
}
