package fr.avenirsesr.portfolio.additionalskill.domain.port.input;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillsPaginated;

public interface AdditionalSkillService {
  AdditionalSkillsPaginated getAdditionalSkills(Integer page, Integer pageSize);
}
