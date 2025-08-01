package fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.shared.domain.port.output.repository.GenericRepositoryPort;
import java.util.List;

public interface AdditionalSkillRepository extends GenericRepositoryPort<AdditionalSkill> {
  List<AdditionalSkill> findByPathSegments_Skill_CodeIn(List<String> skillCodes);
}
