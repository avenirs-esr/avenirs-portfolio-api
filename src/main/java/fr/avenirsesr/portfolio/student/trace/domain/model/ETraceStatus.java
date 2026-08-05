package fr.avenirsesr.portfolio.student.trace.domain.model;

import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import java.util.List;
import lombok.Getter;

@Getter
public enum ETraceStatus {
  ASSOCIATED_EVALUATED(List.of(ESkillLevelStatus.VALIDATED, ESkillLevelStatus.FAILED)),
  ASSOCIATED_IN_EVALUATION(List.of(ESkillLevelStatus.UNDER_REVIEW)),
  ASSOCIATED_NOT_EVALUATED(
      List.of(ESkillLevelStatus.NOT_STARTED, ESkillLevelStatus.TO_BE_EVALUATED)),
  ASSOCIATED_WITH_DECLARED_SKILL(List.of()),
  ;

  private final List<ESkillLevelStatus> skillLevelStatuses;

  ETraceStatus(List<ESkillLevelStatus> skillLevelStatuses) {
    this.skillLevelStatuses = skillLevelStatuses;
  }
}
