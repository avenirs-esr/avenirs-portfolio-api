package fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.shared.domain.port.output.repository.GenericRepositoryPort;

public interface AdditionalSkillProgressRepository
    extends GenericRepositoryPort<AdditionalSkillProgress> {
  boolean additionalSkillProgressAlreadyExists(AdditionalSkillProgress additionalSkillProgress);
}
