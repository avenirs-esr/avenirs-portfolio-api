package fr.avenirsesr.portfolio.student.activity.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EFeedbackStatus;
import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Feedback extends AvenirsBaseModel {
  private final DeclaredActivity declaredActivity;

  @Getter(AccessLevel.NONE)
  private String reflexion;

  @Getter(AccessLevel.NONE)
  private String feedback;

  private EFeedbackStatus status;
  private int iteration;
  private List<Trace> associatedTraces;
  private List<DeclaredSkillProgress> associatedDeclaredSkills;
  private List<File> attachments;

  private Feedback(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      DeclaredActivity declaredActivity,
      String reflexion,
      String feedback,
      EFeedbackStatus status,
      int iteration,
      List<Trace> associatedTraces,
      List<DeclaredSkillProgress> associatedDeclaredSkills,
      List<File> attachments) {
    super(id, createdAt, updatedAt);
    this.reflexion = reflexion;
    this.feedback = feedback;
    this.status = status;
    this.iteration = iteration;
    this.associatedTraces = associatedTraces;
    this.associatedDeclaredSkills = associatedDeclaredSkills;
    this.attachments = new ArrayList<>(attachments == null ? List.of() : attachments);
    this.declaredActivity = declaredActivity;
  }

  public static Feedback create(
      DeclaredActivity declaredActivity,
      String reflexion,
      List<Trace> associatedTraces,
      List<DeclaredSkillProgress> associatedDeclaredSkills,
      int iteration) {
    return new Feedback(
        UUID.randomUUID(),
        Instant.now(),
        Instant.now(),
        declaredActivity,
        reflexion,
        null,
        EFeedbackStatus.NEW,
        iteration,
        associatedTraces,
        associatedDeclaredSkills,
        List.of());
  }

  public static Feedback toDomain(
      UUID id,
      Instant createdAt,
      Instant updatedAt,
      DeclaredActivity declaredActivity,
      String reflexion,
      String feedback,
      EFeedbackStatus status,
      int iteration,
      List<Trace> associatedTraces,
      List<DeclaredSkillProgress> associatedDeclaredSkills,
      List<File> attachments) {
    return new Feedback(
        id,
        createdAt,
        updatedAt,
        declaredActivity,
        reflexion,
        feedback,
        status,
        iteration,
        associatedTraces,
        associatedDeclaredSkills,
        attachments);
  }

  public void addAttachment(File attachment) {
    this.attachments.add(attachment);
  }

  public void removeAttachment(UUID attachmentId) {
    this.attachments.removeIf(attachment -> attachment.getId().equals(attachmentId));
  }

  public Optional<File> findAttachment(UUID attachmentId) {
    return this.attachments.stream()
        .filter(attachment -> attachment.getId().equals(attachmentId))
        .findFirst();
  }

  public Optional<String> getReflexion() {
    return Optional.ofNullable(reflexion);
  }

  public Optional<String> getFeedback() {
    return Optional.ofNullable(feedback);
  }
}
