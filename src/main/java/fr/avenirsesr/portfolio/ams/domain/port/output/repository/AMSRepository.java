package fr.avenirsesr.portfolio.ams.domain.port.output.repository;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;
import fr.avenirsesr.portfolio.shared.domain.port.output.repository.GenericRepositoryPort;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;
import java.util.List;
import java.util.UUID;

public interface AMSRepository extends GenericRepositoryPort<AMS> {
  PagedResult<AMS> findByUserIdViaCohortsAndSkillLevelProgresses(
      UUID userId, List<SkillLevelProgress> skillLevelProgresses, PageCriteria pageCriteria);
}
