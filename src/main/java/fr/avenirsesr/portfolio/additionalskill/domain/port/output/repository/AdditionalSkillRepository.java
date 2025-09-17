package fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import java.util.List;
import java.util.UUID;

public interface AdditionalSkillRepository extends GenericRepositoryPort<AdditionalSkill> {
  List<AdditionalSkill> findByPathSegmentsSkillCodeIn(List<String> skillCodes);

  List<AdditionalSkill> findAllByIds(List<UUID> ids);
}
