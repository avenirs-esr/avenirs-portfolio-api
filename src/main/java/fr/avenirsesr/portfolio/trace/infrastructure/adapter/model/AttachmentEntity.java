package fr.avenirsesr.portfolio.trace.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.shared.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.shared.infrastructure.adapter.model.AvenirsBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "attachment")
@NoArgsConstructor
@Getter
@Setter
public class AttachmentEntity extends AvenirsBaseEntity {

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

  @Column(nullable = false, name = "uploaded_at")
  private Instant uploadedAt;

  @Column(nullable = false)
  private String uri;

  private AttachmentEntity(
      UUID id,
      TraceEntity trace,
      String name,
      EFileType attachmentType,
      long size,
      int version,
      boolean isActiveVersion,
      Instant uploadedAt,
      String uri) {
    this.setId(id);
    this.trace = trace;
    this.name = name;
    this.attachmentType = attachmentType;
    this.size = size;
    this.version = version;
    this.isActiveVersion = isActiveVersion;
    this.uploadedAt = uploadedAt;
    this.uri = uri;
  }

  public static AttachmentEntity of(
      UUID id,
      TraceEntity trace,
      String name,
      EFileType attachmentType,
      long size,
      int version,
      boolean isActiveVersion,
      Instant uploadedAt,
      String uri) {
    return new AttachmentEntity(
        id, trace, name, attachmentType, size, version, isActiveVersion, uploadedAt, uri);
  }
}
