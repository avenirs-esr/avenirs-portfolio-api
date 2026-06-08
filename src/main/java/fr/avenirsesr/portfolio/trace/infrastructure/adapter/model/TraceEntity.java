package fr.avenirsesr.portfolio.trace.infrastructure.adapter.model;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.*;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.DeletableAvenirsBaseEntity;
import fr.avenirsesr.portfolio.common.language.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.FileEntity;
import fr.avenirsesr.portfolio.trace.domain.model.enums.ETraceAuthorType;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "trace",
    indexes = {
      @Index(
          name = "idx_trace_user_deleted_created",
          columnList = "user_id, deleted_at, created_at"),
      @Index(
          name = "idx_trace_user_deleted_updated",
          columnList = "user_id, deleted_at, updated_at, created_at")
    })
@NoArgsConstructor
@Getter
@Setter
public class TraceEntity extends DeletableAvenirsBaseEntity {
  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @Column(nullable = false, length = TITLE_LENGTH)
  private String title;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ELanguage language;

  @Column(nullable = false, name = "author_type")
  @Enumerated(EnumType.STRING)
  private ETraceAuthorType authorType;

  @Size(
      max = AI_JUSTIFICATION_LENGTH,
      message = "ai use justification can not exceed {max} characters")
  @Column(name = "ai_use_justification")
  private String aiUseJustification;

  @Size(max = PERSONAL_NOTE_LENGTH, message = "personal note can not exceed {max} characters")
  @Column(name = "personal_note")
  private String personalNote;

  @Size(max = LINK_LENGTH, message = "link can not exceed {max} characters")
  @Column(name = "link")
  private String link;

  @OneToOne private FileEntity attachment;

  private TraceEntity(
      UUID id,
      UserEntity user,
      String title,
      ELanguage language,
      ETraceAuthorType authorType,
      String aiUseJustification,
      String personalNote,
      String link,
      FileEntity attachment,
      Instant createdAt,
      Instant updatedAt,
      Instant deletedAt) {
    this.setId(id);
    this.setDeletedAt(deletedAt);
    this.setCreatedAt(createdAt);
    this.setUpdatedAt(updatedAt);
    this.user = user;
    this.title = title;
    this.language = language;
    this.authorType = authorType;
    this.aiUseJustification = aiUseJustification;
    this.personalNote = personalNote;
    this.link = link;
    this.attachment = attachment;
  }

  public static TraceEntity of(
      UUID id,
      UserEntity user,
      String title,
      ELanguage language,
      ETraceAuthorType authorType,
      String aiUseJustification,
      String personalNote,
      String link,
      FileEntity attachment,
      Instant createdAt,
      Instant updatedAt,
      Instant deletedAt) {
    return new TraceEntity(
        id,
        user,
        title,
        language,
        authorType,
        aiUseJustification,
        personalNote,
        link,
        attachment,
        createdAt,
        updatedAt,
        deletedAt);
  }
}
