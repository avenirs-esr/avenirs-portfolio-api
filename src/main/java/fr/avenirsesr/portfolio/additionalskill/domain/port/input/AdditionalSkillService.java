package fr.avenirsesr.portfolio.additionalskill.domain.port.input;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;

public interface AdditionalSkillService {
  PagedResult<AdditionalSkill> getAdditionalSkills(PageCriteria criteria);

  PagedResult<AdditionalSkill> searchAdditionalSkills(String keyword, PageCriteria criteria);
}
