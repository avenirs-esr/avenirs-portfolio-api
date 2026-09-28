package fr.avenirsesr.portfolio.student.activity.domain.data;

import fr.avenirsesr.portfolio.student.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.skill.domain.data.DeclaredSkillProgressDetails;
import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import java.util.List;

public record FeedbackAssociationsData(
    List<Trace> traces,
    List<DeclaredSkillProgressDetails> declaredSkills,
    List<DeclaredExperience> declaredExperiences) {

  public static FeedbackAssociationsData empty() {
    return new FeedbackAssociationsData(List.of(), List.of(), List.of());
  }
}
