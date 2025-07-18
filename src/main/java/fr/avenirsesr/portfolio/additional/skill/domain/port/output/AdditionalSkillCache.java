package fr.avenirsesr.portfolio.additional.skill.domain.port.output;

import fr.avenirsesr.portfolio.additional.skill.domain.model.AdditionalSkillsPaginated;

public interface AdditionalSkillCache {
  AdditionalSkillsPaginated findAll(Integer page, Integer pageSize);
}
