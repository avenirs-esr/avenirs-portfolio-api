package fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import java.util.Optional;
import java.util.UUID;

public interface AdditionalSkillRepository extends GenericRepositoryPort<AdditionalSkill> {
  Optional<AdditionalSkill> findByExternalSkillId(UUID externalSkillId);
}
