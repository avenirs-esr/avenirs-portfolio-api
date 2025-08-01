package fr.avenirsesr.portfolio.additionalskill.domain.port.output;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillPagedResult;
import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import java.util.List;

public interface OpenSearch {

  void cleanAndCreateAdditionalSkillIndex();

  void indexAll(List<AdditionalSkill> additionalSkillList);

  AdditionalSkillPagedResult search(String keyword, PageCriteria pageCriteria);
}
