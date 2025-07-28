package fr.avenirsesr.portfolio.file.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.file.domain.model.EFileType;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "trace_attachment")
@NoArgsConstructor
@Getter
@Setter
public class TraceAttachmentEntity extends AvenirsBaseEntity {

  @ManyToOne(optional = false)
  private TraceEntity trace;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, name = "attachment_type")
  @Enumerated(EnumType.STRING)
  private EFileType attachmentType;

  @Column(nullable = false)
  private long size;

  @Column(nullable = false)
  private int version;

  @Column(nullable = false, name = "is_active_version")
  private boolean isActiveVersion;

  @Column(nullable = false)
  private String uri;

  @ManyToOne()
  @JoinColumn(name = "uploaded_by", nullable = false)
  private UserEntity uploadedBy;

  @Column(nullable = false, name = "uploaded_at")
  private Instant uploadedAt;

  private TraceAttachmentEntity(
      UUID id,
      TraceEntity trace,
      String name,
      EFileType attachmentType,
      long size,
      int version,
      boolean isActiveVersion,
      String uri,
      UserEntity uploadedBy,
      Instant uploadedAt) {
    this.setId(id);
    this.trace = trace;
    this.name = name;
    this.attachmentType = attachmentType;
    this.size = size;
    this.version = version;
    this.isActiveVersion = isActiveVersion;
    this.uri = uri;
    this.uploadedBy = uploadedBy;
    this.uploadedAt = uploadedAt;
  }

  public static TraceAttachmentEntity of(
      UUID id,
      TraceEntity trace,
      String name,
      EFileType attachmentType,
      long size,
      int version,
      boolean isActiveVersion,
      String uri,
      UserEntity uploadedBy,
      Instant uploadedAt) {
    return new TraceAttachmentEntity(
        id,
        trace,
        name,
        attachmentType,
        size,
        version,
        isActiveVersion,
        uri,
        uploadedBy,
        uploadedAt);
  }
}
