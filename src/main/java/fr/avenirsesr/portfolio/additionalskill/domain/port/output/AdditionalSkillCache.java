package fr.avenirsesr.portfolio.additionalskill.domain.port.output;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;

public interface AdditionalSkillCache {
  PagedResult<AdditionalSkill> findAll(PageCriteria pageCriteria);

  PagedResult<AdditionalSkill> findBySkillTitle(String keyword, PageCriteria pageCriteria);
}
