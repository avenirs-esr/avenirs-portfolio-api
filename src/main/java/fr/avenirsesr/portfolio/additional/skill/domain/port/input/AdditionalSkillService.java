package fr.avenirsesr.portfolio.additional.skill.domain.port.input;

import fr.avenirsesr.portfolio.additional.skill.domain.model.AdditionalSkillsPaginated;

public interface AdditionalSkillService {
  AdditionalSkillsPaginated getAdditionalSkills(Integer page, Integer pageSize);
}
