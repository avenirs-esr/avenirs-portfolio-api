package fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.student.experience.domain.model.enums.EExperienceType;
import fr.avenirsesr.portfolio.student.skill.domain.model.enums.EDeclaredSkillLevel;
import fr.avenirsesr.portfolio.student.trace.domain.model.enums.ETraceAuthorType;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record AssociationsJson(
    List<TraceSnapshot> traces,
    List<DeclaredSkillProgressSnapshot> declaredSkillProgresses,
    List<DeclaredExperienceSnapshot> declaredExperiences) {
  public AssociationsJson {
    traces = traces == null ? List.of() : traces;
    declaredSkillProgresses = declaredSkillProgresses == null ? List.of() : declaredSkillProgresses;
    declaredExperiences = declaredExperiences == null ? List.of() : declaredExperiences;
  }

  public static AssociationsJson empty() {
    return new AssociationsJson(List.of(), List.of(), List.of());
  }

  public record TraceSnapshot(
      UUID id,
      UUID studentId,
      UUID attachmentId,
      String title,
      ELanguage language,
      ETraceAuthorType authorType,
      String aiUseJustification,
      String personalNote,
      String link,
      Instant createdAt,
      Instant updatedAt) {}

  public record DeclaredSkillProgressSnapshot(
      UUID id,
      UUID studentId,
      UUID skillId,
      EDeclaredSkillLevel level,
      String reflection,
      Instant createdAt,
      Instant updatedAt) {}

  public record DeclaredExperienceSnapshot(
      UUID id,
      UUID studentId,
      String title,
      EExperienceType experienceType,
      String organization,
      String activitySector,
      String location,
      String description,
      String sourceOfInformation,
      String summary,
      String externalLink,
      String result,
      LocalDate startDate,
      LocalDate endDate,
      Instant createdAt,
      Instant updatedAt) {}
}
