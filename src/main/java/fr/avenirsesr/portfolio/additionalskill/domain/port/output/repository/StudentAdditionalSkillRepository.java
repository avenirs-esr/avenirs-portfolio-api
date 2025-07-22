package fr.avenirsesr.portfolio.additionalskill.domain.port.output.repository;

import fr.avenirsesr.portfolio.additionalskill.domain.model.enums.EAdditionalSkillType;
import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.user.domain.model.Student;

public interface StudentAdditionalSkillRepository {
  void saveAdditionalSkill(
      Student student,
      String additionalSkillId,
      EAdditionalSkillType type,
      ESkillLevelStatus level);
}
