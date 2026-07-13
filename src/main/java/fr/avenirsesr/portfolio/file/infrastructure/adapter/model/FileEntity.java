package fr.avenirsesr.portfolio.file.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileType;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "file")
@NoArgsConstructor
public class FileEntity extends AvenirsBaseEntity {
  @Column(nullable = false, name = "file_type")
  @Enumerated(EnumType.STRING)
  private EFileType fileType;

  @Column(nullable = false, name = "file_name")
  private String fileName;

  @Column(nullable = false)
  private long size;

  @Column(nullable = false)
  private String uri;

  @ManyToOne()
  @JoinColumn(name = "uploaded_by", nullable = false)
  private UserEntity uploadedBy;

  @Column(nullable = false, name = "uploaded_at")
  private Instant uploadedAt;

  @Column(nullable = false, name = "is_restricted")
  private boolean isRestricted;

  private FileEntity(
      UUID id,
      EFileType fileType,
      String fileName,
      long size,
      String uri,
      UserEntity uploadedBy,
      Instant uploadedAt,
      boolean isRestricted,
      Instant createdAt,
      Instant updatedAt) {
    this.setId(id);
    this.fileType = fileType;
    this.fileName = fileName;
    this.size = size;
    this.uri = uri;
    this.uploadedBy = uploadedBy;
    this.uploadedAt = uploadedAt;
    this.isRestricted = isRestricted;
    this.setCreatedAt(createdAt);
    this.setUpdatedAt(updatedAt);
  }

  public static FileEntity of(
      UUID id,
      EFileType fileType,
      String fileName,
      long size,
      String uri,
      UserEntity uploadedBy,
      Instant uploadedAt,
      boolean isRestricted,
      Instant createdAt,
      Instant updatedAt) {
    return new FileEntity(
        id,
        fileType,
        fileName,
        size,
        uri,
        uploadedBy,
        uploadedAt,
        isRestricted,
        createdAt,
        updatedAt);
  }
}
