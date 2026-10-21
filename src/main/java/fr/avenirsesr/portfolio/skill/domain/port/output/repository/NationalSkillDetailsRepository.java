package fr.avenirsesr.portfolio.skill.domain.port.output.repository;

import fr.avenirsesr.portfolio.common.data.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.common.skill.domain.model.enums.ESkillKind;
import fr.avenirsesr.portfolio.skill.domain.model.NationalSkillDetails;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface NationalSkillDetailsRepository
    extends GenericRepositoryPort<NationalSkillDetails> {
  Map<UUID, ESkillKind> findKindsBySkillIds(List<UUID> skillIds);

  void deleteAllBySkillIds(List<UUID> skillIds);
}
