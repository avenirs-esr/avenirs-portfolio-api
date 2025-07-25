package fr.avenirsesr.portfolio.additionalskill.domain.port.output;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;
import java.util.List;
import java.util.UUID;

public interface AdditionalSkillCache {
  PagedResult<AdditionalSkill> findAll(PageCriteria pageCriteria);

  PagedResult<AdditionalSkill> findBySkillTitle(String keyword, PageCriteria pageCriteria);

  AdditionalSkill findById(UUID id);

  List<AdditionalSkill> findAllByIds(List<UUID> ids);
}
