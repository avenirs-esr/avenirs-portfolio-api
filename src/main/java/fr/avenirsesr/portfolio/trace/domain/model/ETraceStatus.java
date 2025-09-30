package fr.avenirsesr.portfolio.trace.domain.model;

import fr.avenirsesr.portfolio.ams.domain.model.enums.EAmsStatus;
import fr.avenirsesr.portfolio.program.domain.model.enums.ESkillLevelStatus;
import java.util.List;
import lombok.Getter;

@Getter
public enum ETraceStatus {
  ASSOCIATED_EVALUATED(
      List.of(EAmsStatus.COMPLETED),
      List.of(ESkillLevelStatus.VALIDATED, ESkillLevelStatus.FAILED)),
  ASSOCIATED_IN_EVALUATION(List.of(EAmsStatus.SUBMITTED), List.of(ESkillLevelStatus.UNDER_REVIEW)),
  ASSOCIATED_NOT_EVALUATED(
      List.of(EAmsStatus.NOT_STARTED, EAmsStatus.IN_PROGRESS),
      List.of(ESkillLevelStatus.NOT_STARTED, ESkillLevelStatus.TO_BE_EVALUATED)),
  ASSOCIATED_WITH_ADDITIONAL_SKILL(List.of(), List.of()),
  ;

  private final List<EAmsStatus> amsStatuses;
  private final List<ESkillLevelStatus> skillLevelStatuses;

  ETraceStatus(List<EAmsStatus> amsStatuses, List<ESkillLevelStatus> skillLevelStatuses) {
    this.amsStatuses = amsStatuses;
    this.skillLevelStatuses = skillLevelStatuses;
  }
}
