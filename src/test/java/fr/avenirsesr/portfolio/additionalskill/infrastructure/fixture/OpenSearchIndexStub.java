package fr.avenirsesr.portfolio.additionalskill.infrastructure.fixture;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillPagedResult;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.OpenSearchIndex;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PageInfo;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class OpenSearchIndexStub implements OpenSearchIndex {

  @Override
  public void cleanAndCreateAdditionalSkillIndex() {
    // No-op (désactivé pendant les tests)
  }

  @Override
  public void indexAll(List<AdditionalSkill> additionalSkillList) {
    // No-op
  }

  @Override
  public AdditionalSkillPagedResult search(String keyword, PageCriteria pageCriteria) {
    return new AdditionalSkillPagedResult(List.of(), new PageInfo(0, 8, 0));
  }
}
