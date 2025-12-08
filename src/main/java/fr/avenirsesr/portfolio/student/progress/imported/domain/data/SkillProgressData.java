package fr.avenirsesr.portfolio.student.progress.imported.domain.data;

import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.program.domain.model.Skill;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.StudentProgress;
import java.util.Comparator;

public record SkillProgressData(
    Skill skill,
    StudentProgress studentProgress,
    SkillLevelProgressWithTraceCountData currentSkillLevelProgress) {

  public static Comparator<SkillProgressData> comparatorOf(SortCriteria sortCriteria) {
    Comparator<SkillProgressData> comparator =
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
