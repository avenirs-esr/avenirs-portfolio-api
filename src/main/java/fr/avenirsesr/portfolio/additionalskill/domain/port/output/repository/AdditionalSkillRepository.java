package fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import java.util.List;

public interface AdditionalSkillRepository extends GenericRepositoryPort<AdditionalSkill> {
  List<AdditionalSkill> findAllByExternalId(List<String> skillCodes);

  int countAll(EAdditionalSkillType type);
}
