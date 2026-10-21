package fr.avenirsesr.portfolio.student.skill.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.student.skill.domain.data.SkillProgressData;
import fr.avenirsesr.portfolio.student.skill.domain.data.SkillProgressDetails;
import fr.avenirsesr.portfolio.student.skill.domain.model.SkillProgress;
import fr.avenirsesr.portfolio.student.skill.domain.model.enums.ESkillLevel;
import java.util.List;
import java.util.UUID;

public interface SkillProgressService {

  PagedResult<SkillProgressData> getSkillProgresses(PageCriteria criteria, Boolean isValorized);

  SkillProgressData createSkillProgress(UUID skillId, ESkillLevel level, String reflection);

  SkillProgressData updateSkillProgress(
      UUID skillProgressId, ESkillLevel level, String reflection, boolean valorized);

  SkillProgressDetails getSkillProgressDetails(UUID skillProgressId);

  List<SkillProgressDetails> getSkillProgressDetails(List<SkillProgress> skillProgresses);

  List<SkillProgressData> getSkillProgressData(List<SkillProgress> skillProgresses);

  void deleteSkillProgresses(List<UUID> skillProgressIds);

  PagedResult<SkillProgress> searchSkillProgress(String keyword, PageCriteria pageCriteria);

  List<SkillProgress> findAllSkillProgressesByIds(List<UUID> ids);

  List<UUID> getSkillIds();
}
