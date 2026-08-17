package fr.avenirsesr.portfolio.student.activity.domain.data;

import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.student.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.activity.domain.model.enums.EFeedbackStatus;
import fr.avenirsesr.portfolio.student.skill.domain.model.DeclaredSkillProgress;
import fr.avenirsesr.portfolio.student.trace.domain.model.Trace;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Represents the details of a feedback. The {@code feedback} and {@code attachments} fields may be
 * respectively {@code null} and empty when the feedback has not yet been submitted (status other
 * than {@link EFeedbackStatus#SUBMITTED}), in order to hide the staff answer from the student until
 * the review is finalised.
 */
public record FeedbackData(
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
    List<File> attachments) {}
