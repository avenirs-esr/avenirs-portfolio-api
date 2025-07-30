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
@Table(name = "trace_attachment")
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
      Instant uploadedAt) {
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
      Instant uploadedAt) {
    return new TraceAttachmentEntity(
        id, trace, name, fileType, size, version, isActiveVersion, uri, uploadedBy, uploadedAt);
  }
}
