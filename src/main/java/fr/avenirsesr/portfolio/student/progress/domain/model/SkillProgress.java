package fr.avenirsesr.portfolio.student.progress.domain.model;

import fr.avenirsesr.portfolio.program.domain.model.Skill;
import fr.avenirsesr.portfolio.shared.domain.model.SortCriteria;
import java.util.Comparator;

public record SkillProgress(
    Skill skill, StudentProgress studentProgress, SkillLevelProgress currentSkillLevelProgress) {

  public static Comparator<SkillProgress> comparatorOf(SortCriteria sortCriteria) {
    Comparator<SkillProgress> comparator =
        switch (sortCriteria.field()) {
          case NAME -> Comparator.comparing(slp -> slp.skill().getName());
          case DATE ->
              Comparator.comparing(
                  skillProgress -> skillProgress.currentSkillLevelProgress().getStartDate());
        };

    comparator =
        switch (sortCriteria.order()) {
          case ASC -> comparator;
          case DESC -> comparator.reversed();
        };

    return comparator;
  }
}
