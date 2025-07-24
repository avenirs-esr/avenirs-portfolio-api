package fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillProgress;

public interface AdditionalSkillProgressRepository {
  void save(AdditionalSkillProgress additionalSkillProgress);
}
