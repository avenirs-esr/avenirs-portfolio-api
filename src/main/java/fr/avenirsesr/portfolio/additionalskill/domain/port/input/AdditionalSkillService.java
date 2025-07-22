package fr.avenirsesr.portfolio.additionalskill.domain.port.input;

import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkill;
import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;
import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.user.domain.model.Student;

public interface AdditionalSkillService {
  PagedResult<AdditionalSkill> getAdditionalSkills(PageCriteria criteria);

  PagedResult<AdditionalSkill> searchAdditionalSkills(String keyword, PageCriteria criteria);

  void saveAdditionalSkills(
      Student student,
      String additionalSkillId,
      EAdditionalSkillType type,
      ESkillLevelStatus level);
}
