package fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.EntityGrapher;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.mapper.FileMapper;
import fr.avenirsesr.portfolio.student.activity.domain.model.Feedback;
import fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.model.AssociationsJson;
import fr.avenirsesr.portfolio.student.activity.infrastructure.adapter.model.FeedbackEntity;
import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkill;
import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FeedbackMapper implements Mapper<FeedbackEntity, Feedback> {

  public static final FeedbackMapper INSTANCE = new FeedbackMapper();

  @Override
  public FeedbackEntity fromDomain(Feedback feedback) {
    FeedbackEntity entity =
        new FeedbackEntity(
            DeclaredActivityMapper.INSTANCE.fromDomain(feedback.getDeclaredActivity()),
            feedback.getReflexion().orElse(null),
            feedback.getFeedback().orElse(null),
            feedback.getStatus(),
            feedback.getIteration(),
            buildAssociations(feedback),
            feedback.getAttachments().stream().map(FileMapper.INSTANCE::fromDomain).toList());
    entity.setId(feedback.getId());
    return entity;
  }

  private AssociationsJson buildAssociations(Feedback feedback) {
    List<AssociationsJson.TraceSnapshot> traces =
        feedback.getAssociatedTraces().stream()
            .map(
                t ->
                    new AssociationsJson.TraceSnapshot(
                        t.getId(),
                        t.getStudent().getId(),
                        t.getAttachment().map(File::getId).orElse(null),
                        t.getTitle(),
                        t.getLanguage(),
                        t.getAuthorType(),
                        t.getAiUseJustification().orElse(null),
                        t.getPersonalNote().orElse(null),
                        t.getLink().orElse(null),
                        t.getCreatedAt(),
                        t.getUpdatedAt()))
            .toList();

    List<AssociationsJson.DeclaredSkillProgressSnapshot> progresses =
        feedback.getAssociatedDeclaredSkills().stream()
            .map(
                p ->
                    new AssociationsJson.DeclaredSkillProgressSnapshot(
                        p.getId(),
                        p.getStudent().getId(),
                        p.getSkill().getId(),
                        p.getLevel(),
                        p.getReflection(),
                        p.getCreatedAt(),
                        p.getUpdatedAt()))
            .toList();

    return new AssociationsJson(traces, progresses);
  }

  @Override
  public Feedback toDomain(FeedbackEntity entity) {
    throw new UnsupportedOperationException(
        "FeedbackMapper.toDomain requiers ths objects File, Student and DeclaredSkill loaded."
            + " Use toDomain(entity, files, students, skills).");
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

    AssociationsJson associations = entity.getAssociations();

    List<Trace> traces =
        associations.traces().stream()
            .map(
                snap ->
                    Trace.toDomain(
                        snap.id(),
                        students.get(snap.studentId()),
                        snap.title(),
                        snap.authorType(),
                        snap.aiUseJustification(),
                        snap.personalNote(),
                        snap.link(),
                        snap.attachmentId() != null ? files.get(snap.attachmentId()) : null,
                        snap.createdAt(),
                        snap.updatedAt(),
                        snap.language(),
                        false))
            .toList();

    List<DeclaredSkillProgress> progresses =
        associations.declaredSkillProgresses().stream()
            .map(
                snap ->
                    DeclaredSkillProgress.toDomain(
                        snap.id(),
                        students.get(snap.studentId()),
                        skills.get(snap.skillId()),
                        snap.level(),
                        snap.reflection(),
                        false,
                        snap.createdAt(),
                        snap.updatedAt()))
            .toList();

    return Feedback.toDomain(
        entity.getId(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        DeclaredActivityMapper.INSTANCE.toDomain(entity.getDeclaredActivity()),
        entity.getReflection(),
        entity.getFeedback(),
        entity.getStatus(),
        entity.getIteration(),
        traces,
        progresses,
        entity.getAttachments().stream().map(FileMapper.INSTANCE::toDomain).toList());
  }
}
