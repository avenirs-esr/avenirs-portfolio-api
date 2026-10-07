package fr.avenirsesr.portfolio.student.activity.domain.model;

import fr.avenirsesr.portfolio.student.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.program.domain.model.DeclaredProgram;
import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import java.util.List;

public record FeedbackAssociations(
    List<Trace> traces,
    List<DeclaredSkillProgress> declaredSkills,
    List<DeclaredExperience> declaredExperiences,
    List<DeclaredProgram> declaredPrograms) {

  public FeedbackAssociations {
    traces = traces == null ? List.of() : List.copyOf(traces);
    declaredSkills = declaredSkills == null ? List.of() : List.copyOf(declaredSkills);
    declaredExperiences =
        declaredExperiences == null ? List.of() : List.copyOf(declaredExperiences);
    declaredPrograms = declaredPrograms == null ? List.of() : List.copyOf(declaredPrograms);
  }

  public static FeedbackAssociations empty() {
    return new FeedbackAssociations(List.of(), List.of(), List.of(), List.of());
  }
}
