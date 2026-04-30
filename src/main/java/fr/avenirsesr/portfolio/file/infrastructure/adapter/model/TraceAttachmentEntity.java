package fr.avenirsesr.portfolio.file.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.file.domain.model.shared.EFileType;
import fr.avenirsesr.portfolio.trace.infrastructure.adapter.model.TraceEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "trace_attachment",
    indexes = {
      @Index(name = "idx_trace_attachment_trace", columnList = "trace_id"),
      @Index(
          name = "idx_trace_attachment_trace_active",
          columnList = "trace_id, is_active_version"),
      @Index(name = "idx_trace_attachment_trace_version", columnList = "trace_id, version DESC")
    })
@NoArgsConstructor
@Getter
@Setter
public class TraceAttachmentEntity extends FileEntity {

  @ManyToOne(optional = false)
  private TraceEntity trace;

  @Column(nullable = false)
  private String name;

  private TraceAttachmentEntity(
      UUID id,
      TraceEntity trace,
      String name,
      EFileType fileType,
      long size,
      int version,
      boolean isActiveVersion,
      String uri,
      UserEntity uploadedBy,
      Instant uploadedAt,
      Instant createdAt,
      Instant updatedAt) {
    this.setId(id);
    this.trace = trace;
    this.name = name;
    this.setFileType(fileType);
    this.setSize(size);
    this.setVersion(version);
    this.setActiveVersion(isActiveVersion);
    this.setUri(uri);
    this.setUploadedBy(uploadedBy);
    this.setUploadedAt(uploadedAt);
    this.setCreatedAt(createdAt);
    this.setUpdatedAt(updatedAt);
  }

  public static TraceAttachmentEntity of(
      UUID id,
      TraceEntity trace,
      String name,
      EFileType fileType,
      long size,
      int version,
      boolean isActiveVersion,
      String uri,
      UserEntity uploadedBy,
      Instant uploadedAt,
      Instant createdAt,
      Instant updatedAt) {
    return new TraceAttachmentEntity(
        id,
        trace,
        name,
        fileType,
        size,
        version,
        isActiveVersion,
        uri,
        uploadedBy,
        uploadedAt,
        createdAt,
        updatedAt);
  }
}
