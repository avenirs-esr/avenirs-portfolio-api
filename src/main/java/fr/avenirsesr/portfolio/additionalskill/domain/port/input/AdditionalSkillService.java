package fr.avenirsesr.portfolio.additionalskill.domain.port.input;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillLevel;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.UUID;

public interface AdditionalSkillService {
  PagedResult<AdditionalSkillProgress> getAdditionalSkillsProgresses(
      Student student, PageCriteria criteria);

  AdditionalSkillProgress createAdditionalSkillProgress(
      Student student,
      UUID additionalSkillId,
      EAdditionalSkillType type,
      EAdditionalSkillLevel level);

  PagedResult<AdditionalSkillProgress> search(
      Student student, String keyword, PageCriteria pageCriteria);
}
