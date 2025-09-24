package fr.avenirsesr.portfolio.student.progress.domain.dto;

import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.program.domain.model.Skill;
import fr.avenirsesr.portfolio.student.progress.domain.model.StudentProgress;
import java.util.Comparator;

public record SkillProgressDTO(
    Skill skill,
    StudentProgress studentProgress,
    SkillLevelProgressWithTraceCountDTO currentSkillLevelProgress) {

  public static Comparator<SkillProgressDTO> comparatorOf(SortCriteria sortCriteria) {
    Comparator<SkillProgressDTO> comparator =
        switch (sortCriteria.field()) {
          case NAME -> Comparator.comparing(slp -> slp.skill().getName());
          case DATE ->
              Comparator.comparing(
                  skillProgress ->
                      skillProgress
                          .currentSkillLevelProgress()
                          .skillLevelProgress()
                          .getStartDate());
        };

    comparator =
        switch (sortCriteria.order()) {
          case ASC -> comparator;
          case DESC -> comparator.reversed();
        };

    return comparator;
  }
}
