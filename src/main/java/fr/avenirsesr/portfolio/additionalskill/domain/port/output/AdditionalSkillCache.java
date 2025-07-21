package fr.avenirsesr.portfolio.additionalskill.domain.port.output;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillsPaginated;

public interface AdditionalSkillCache {
  AdditionalSkillsPaginated findAll(Integer page, Integer pageSize);
}
