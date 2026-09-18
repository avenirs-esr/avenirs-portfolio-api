package fr.avenirsesr.portfolio.student.association.domain.data;

import fr.avenirsesr.portfolio.student.activity.domain.data.DeclaredActivityAssociationData;
import fr.avenirsesr.portfolio.student.experience.domain.data.DeclaredExperienceAssociationData;
import fr.avenirsesr.portfolio.student.program.domain.data.DeclaredProgramAssociationData;
import fr.avenirsesr.portfolio.student.skill.domain.data.DeclaredSkillAssociationData;
import fr.avenirsesr.portfolio.student.trace.domain.data.TraceAssociationData;
import java.util.List;
import java.util.stream.Stream;

public record AssociatedElementsData(
    List<TraceAssociationData> traceAssociations,
    List<DeclaredActivityAssociationData> declaredActivityAssociations,
    List<DeclaredSkillAssociationData> declaredSkillAssociations,
    List<DeclaredExperienceAssociationData> declaredExperienceAssociations,
    List<DeclaredProgramAssociationData> declaredProgramAssociations) {

  public static AssociatedElementsData empty() {
    return new AssociatedElementsData(List.of(), List.of(), List.of(), List.of(), List.of());
  }

  public static AssociatedElementsData ofTraces(List<TraceAssociationData> traceAssociations) {
    return new AssociatedElementsData(
        traceAssociations, List.of(), List.of(), List.of(), List.of());
  }

  public static AssociatedElementsData ofDeclaredActivities(
      List<DeclaredActivityAssociationData> declaredActivityAssociations) {
    return new AssociatedElementsData(
        List.of(), declaredActivityAssociations, List.of(), List.of(), List.of());
  }

  public static AssociatedElementsData ofDeclaredSkills(
      List<DeclaredSkillAssociationData> declaredSkillAssociations) {
    return new AssociatedElementsData(
        List.of(), List.of(), declaredSkillAssociations, List.of(), List.of());
  }

  public static AssociatedElementsData ofDeclaredExperiences(
      List<DeclaredExperienceAssociationData> declaredExperienceAssociations) {
    return new AssociatedElementsData(
        List.of(), List.of(), List.of(), declaredExperienceAssociations, List.of());
  }

  public static AssociatedElementsData ofDeclaredPrograms(
      List<DeclaredProgramAssociationData> declaredProgramAssociations) {
    return new AssociatedElementsData(
        List.of(), List.of(), List.of(), List.of(), declaredProgramAssociations);
  }

  public AssociatedElementsData merge(AssociatedElementsData other) {
    return new AssociatedElementsData(
        concat(traceAssociations, other.traceAssociations),
        concat(declaredActivityAssociations, other.declaredActivityAssociations),
        concat(declaredSkillAssociations, other.declaredSkillAssociations),
        concat(declaredExperienceAssociations, other.declaredExperienceAssociations),
        concat(declaredProgramAssociations, other.declaredProgramAssociations));
  }

  private static <T> List<T> concat(List<T> elements, List<T> otherElements) {
    return Stream.concat(elements.stream(), otherElements.stream()).toList();
  }
}
