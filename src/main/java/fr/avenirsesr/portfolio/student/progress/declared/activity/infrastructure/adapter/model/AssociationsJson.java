package fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.declaredskill.domain.model.enums.EDeclaredSkillLevel;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record AssociationsJson(
    List<TraceSnapshot> traces, List<DeclaredSkillProgressSnapshot> declaredSkillProgresses) {
  public record TraceSnapshot(
      UUID id,
      UUID userId,
      UUID attachmentId,
      String title,
      ELanguage language,
      boolean traceAuthorType,
      String aiUseJustification,
      String personalNote,
      String link,
      Instant createdAt,
      Instant updatedAt,
      Instant deletedAt) {}

  public record DeclaredSkillProgressSnapshot(
      UUID id,
      UUID studentId,
      UUID skillId,
      EDeclaredSkillLevel level,
      String reflection,
      Instant createdAt,
      Instant updatedAt) {}
}
