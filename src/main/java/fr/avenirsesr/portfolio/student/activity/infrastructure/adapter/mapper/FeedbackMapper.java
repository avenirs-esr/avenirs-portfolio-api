package fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.EntityGrapher;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.mapper.FileMapper;
import fr.avenirsesr.portfolio.student.activity.domain.model.Feedback;
import fr.avenirsesr.portfolio.student.activity.domain.model.FeedbackAssociations;
import fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.model.AssociationsJson;
import fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.model.FeedbackEntity;
import fr.avenirsesr.portfolio.student.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.program.domain.model.DeclaredProgram;
import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkill;
import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.Map;
import java.util.UUID;

public class FeedbackMapper implements Mapper<FeedbackEntity, Feedback> {

  public static final FeedbackMapper INSTANCE = new FeedbackMapper();

  private AssociationsJson.TraceSnapshot traceToSnapshot(Trace trace) {
    return new AssociationsJson.TraceSnapshot(
        trace.getId(),
        trace.getStudent().getId(),
        trace.getAttachment().map(File::getId).orElse(null),
        trace.getTitle(),
        trace.getLanguage(),
        trace.getAuthorType(),
        trace.getAiUseJustification().orElse(null),
        trace.getPersonalNote().orElse(null),
        trace.getLink().orElse(null),
        trace.getCreatedAt(),
        trace.getUpdatedAt());
  }

  private Trace snapshotToTrace(
      AssociationsJson.TraceSnapshot snapshot, Map<UUID, Student> students, Map<UUID, File> files) {
    return Trace.toDomain(
        snapshot.id(),
        students.get(snapshot.studentId()),
        snapshot.title(),
        snapshot.authorType(),
        snapshot.aiUseJustification(),
        snapshot.personalNote(),
        snapshot.link(),
        snapshot.attachmentId() != null ? files.get(snapshot.attachmentId()) : null,
        snapshot.createdAt(),
        snapshot.updatedAt(),
        snapshot.language(),
        false);
  }

  private AssociationsJson.DeclaredExperienceSnapshot declaredExperienceToSnapshot(
      DeclaredExperience declaredExperience) {
    return new AssociationsJson.DeclaredExperienceSnapshot(
        declaredExperience.getId(),
        declaredExperience.getStudent().getId(),
        declaredExperience.getTitle(),
        declaredExperience.getExperienceType(),
        declaredExperience.getOrganization(),
        declaredExperience.getActivitySector(),
        declaredExperience.getLocation(),
        declaredExperience.getDescription(),
        declaredExperience.getSourceOfInformation(),
        declaredExperience.getSummary(),
        declaredExperience.getExternalLink(),
        declaredExperience.getResult(),
        declaredExperience.getStartDate(),
        declaredExperience.getEndDate(),
        declaredExperience.getCreatedAt(),
        declaredExperience.getUpdatedAt());
  }

  private DeclaredExperience snapshotToDeclaredExperience(
      AssociationsJson.DeclaredExperienceSnapshot snapshot, Map<UUID, Student> students) {
    return DeclaredExperience.toDomain(
        snapshot.id(),
        snapshot.createdAt(),
        snapshot.updatedAt(),
        students.get(snapshot.studentId()),
        snapshot.title(),
        snapshot.experienceType(),
        snapshot.organization(),
        snapshot.activitySector(),
        snapshot.location(),
        snapshot.description(),
        snapshot.sourceOfInformation(),
        snapshot.summary(),
        snapshot.externalLink(),
        snapshot.result(),
        snapshot.startDate(),
        snapshot.endDate(),
        false);
  }

  private AssociationsJson.DeclaredSkillProgressSnapshot declaredSkillProgressToSnapshot(
      DeclaredSkillProgress declaredSkill) {
    return new AssociationsJson.DeclaredSkillProgressSnapshot(
        declaredSkill.getId(),
        declaredSkill.getStudent().getId(),
        declaredSkill.getSkill().getId(),
        declaredSkill.getLevel(),
        declaredSkill.getReflection(),
        declaredSkill.getCreatedAt(),
        declaredSkill.getUpdatedAt());
  }

  private DeclaredSkillProgress snapshotToDeclaredSkillProgress(
      AssociationsJson.DeclaredSkillProgressSnapshot snapshot,
      Map<UUID, Student> students,
      Map<UUID, DeclaredSkill> skills) {
    return DeclaredSkillProgress.toDomain(
        snapshot.id(),
        students.get(snapshot.studentId()),
        skills.get(snapshot.skillId()),
        snapshot.level(),
        snapshot.reflection(),
        false,
        snapshot.createdAt(),
        snapshot.updatedAt());
  }

  private AssociationsJson.DeclaredprogramSnapshot declaredprogramSnapshot(
      DeclaredProgram declaredProgram) {
    return new AssociationsJson.DeclaredprogramSnapshot(
        declaredProgram.getId(),
        declaredProgram.getStudent().getId(),
        declaredProgram.getStatus(),
        declaredProgram.getTitle(),
        declaredProgram.getDescription(),
        declaredProgram.getOrganization(),
        declaredProgram.getResult(),
        declaredProgram.getSourceOfInformation(),
        declaredProgram.getStartDate(),
        declaredProgram.getEndDate(),
        declaredProgram.getCreatedAt(),
        declaredProgram.getUpdatedAt());
  }

  private DeclaredProgram snapshotToDeclaredProgram(
      AssociationsJson.DeclaredprogramSnapshot snapshot, Map<UUID, Student> students) {

    return DeclaredProgram.toDomain(
        snapshot.id(),
        students.get(snapshot.studentId()),
        snapshot.status(),
        snapshot.title(),
        snapshot.description(),
        snapshot.organization(),
        snapshot.result(),
        snapshot.sourceOfInformation(),
        snapshot.startDate(),
        snapshot.endDate(),
        false,
        snapshot.createdAt(),
        snapshot.updatedAt());
  }

  private AssociationsJson associationsToSnapshots(FeedbackAssociations associations) {
    return new AssociationsJson(
        associations.traces().stream().map(this::traceToSnapshot).toList(),
        associations.declaredSkills().stream().map(this::declaredSkillProgressToSnapshot).toList(),
        associations.declaredExperiences().stream()
            .map(this::declaredExperienceToSnapshot)
            .toList(),
        associations.declaredPrograms().stream().map(this::declaredprogramSnapshot).toList());
  }

  private FeedbackAssociations snapshotsToAssociations(
      Map<UUID, Student> students,
      Map<UUID, DeclaredSkill> skills,
      Map<UUID, File> files,
      AssociationsJson associationsJson) {
    return new FeedbackAssociations(
        associationsJson.traces().stream()
            .map(snapshot -> snapshotToTrace(snapshot, students, files))
            .toList(),
        associationsJson.declaredSkillProgresses().stream()
            .map(snapshot -> snapshotToDeclaredSkillProgress(snapshot, students, skills))
            .toList(),
        associationsJson.declaredExperiences().stream()
            .map(snapshot -> snapshotToDeclaredExperience(snapshot, students))
            .toList(),
        associationsJson.declaredPrograms().stream()
            .map(snapshot -> snapshotToDeclaredProgram(snapshot, students))
            .toList());
  }

  @Override
  public FeedbackEntity fromDomain(Feedback feedback) {
    FeedbackEntity entity =
        new FeedbackEntity(
            DeclaredActivityMapper.INSTANCE.fromDomain(feedback.getDeclaredActivity()),
            feedback.getReflexion().orElse(null),
            feedback.getFeedback().orElse(null),
            feedback.getStatus(),
            feedback.getIteration(),
            associationsToSnapshots(feedback.getAssociations()),
            feedback.getAttachments().stream().map(FileMapper.INSTANCE::fromDomain).toList());

    entity.setId(feedback.getId());
    return entity;
  }

  @Override
  public Feedback toDomain(FeedbackEntity entity) {
    throw new UnsupportedOperationException(
        "FeedbackMapper.toDomain requires the objects File, Student and DeclaredSkill loaded. Use"
            + " toDomain(entity, files, students, skills).");
  }

  @Override
  public Feedback toDomain(FeedbackEntity entity, EntityGrapher<?> graph) {
    throw new UnsupportedOperationException("Use toDomain(entity, files, students, skills).");
  }

  public Feedback toDomain(
      FeedbackEntity entity,
      Map<UUID, File> files,
      Map<UUID, Student> students,
      Map<UUID, DeclaredSkill> skills) {
    return Feedback.toDomain(
        entity.getId(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        DeclaredActivityMapper.INSTANCE.toDomain(entity.getDeclaredActivity()),
        entity.getReflection(),
        entity.getFeedback(),
        entity.getStatus(),
        entity.getIteration(),
        snapshotsToAssociations(students, skills, files, entity.getAssociations()),
        entity.getAttachments().stream().map(FileMapper.INSTANCE::toDomain).toList());
  }
}
