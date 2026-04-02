package fr.avenirsesr.portfolio.student.progress.imported.domain.port.input;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.SkillLevelProgress;
import java.util.List;
import java.util.UUID;

public interface SkillLevelProgressService {
  List<SkillLevelProgress> getSkillLevelsBySkillId(UUID skillId);

  PagedResult<SkillLevelProgress> searchSkillLevel(String keyword, PageCriteria pageCriteria);

  List<SkillLevelProgress> getSkillLevelProgressesLinkedWithAMS(AMS ams);
}
