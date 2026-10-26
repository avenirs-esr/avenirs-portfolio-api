package fr.avenirsesr.portfolio.student.activity.domain.data;

import fr.avenirsesr.portfolio.student.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.skill.domain.data.SkillProgressDetails;
import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import java.util.List;

public record DeclaredActivityAssociationsData(
    List<Trace> traces,
    List<SkillProgressDetails> skillProgresses,
    List<DeclaredExperience> declaredExperiences) {

  public static DeclaredActivityAssociationsData empty() {
    return new DeclaredActivityAssociationsData(List.of(), List.of(), List.of());
  }
}
