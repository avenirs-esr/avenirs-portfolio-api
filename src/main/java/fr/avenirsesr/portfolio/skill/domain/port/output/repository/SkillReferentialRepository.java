package fr.avenirsesr.portfolio.skill.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillReferentialType;
import fr.avenirsesr.portfolio.skill.domain.model.SkillReferential;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SkillReferentialRepository extends GenericRepositoryPort<SkillReferential> {
  Optional<SkillReferential> findNationalByType(ESkillReferentialType type);

  List<SkillReferential> findAllNational();

  List<SkillReferential> findAllByInstitutionId(UUID institutionId);
}
