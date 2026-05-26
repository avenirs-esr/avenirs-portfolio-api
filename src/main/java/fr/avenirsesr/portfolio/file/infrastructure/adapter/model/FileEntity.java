package fr.avenirsesr.portfolio.file.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.file.domain.model.enums.EFileCategory;
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
@Table(
    name = "file",
    indexes = {@Index(name = "idx_file_element_id", columnList = "element_id")})
@NoArgsConstructor
public class FileEntity extends AvenirsBaseEntity {
  @Column(nullable = false, name = "file_type")
  @Enumerated(EnumType.STRING)
  private EFileType fileType;

  @Column(nullable = false, name = "file_category")
  @Enumerated(EnumType.STRING)
  private EFileCategory fileCategory;

  @Column(nullable = false, name = "file_name")
  private String fileName;

  @Column(nullable = false)
  private long size;

  @Column(nullable = false)
  private int version;

  @Column(nullable = false)
  private String uri;

  @Column(nullable = false, name = "element_id")
  private UUID elementId;

  @ManyToOne()
  @JoinColumn(name = "uploaded_by", nullable = false)
  private UserEntity uploadedBy;

  @Column(nullable = false, name = "uploaded_at")
  private Instant uploadedAt;

  private FileEntity(
      UUID id,
      EFileType fileType,
      EFileCategory fileCategory,
      String fileName,
      long size,
      int version,
      String uri,
      UUID elementId,
      UserEntity uploadedBy,
      Instant uploadedAt,
      Instant createdAt,
      Instant updatedAt) {
    this.setId(id);
    this.fileType = fileType;
    this.fileCategory = fileCategory;
    this.fileName = fileName;
    this.size = size;
    this.version = version;
    this.uri = uri;
    this.elementId = elementId;
    this.uploadedBy = uploadedBy;
    this.uploadedAt = uploadedAt;
    this.setCreatedAt(createdAt);
    this.setUpdatedAt(updatedAt);
  }

  public static FileEntity of(
      UUID id,
      EFileType fileType,
      EFileCategory fileCategory,
      String fileName,
      long size,
      int version,
      String uri,
      UUID elementId,
      UserEntity uploadedBy,
      Instant uploadedAt,
      Instant createdAt,
      Instant updatedAt) {
    return new FileEntity(
        id,
        fileType,
        fileCategory,
        fileName,
        size,
        version,
        uri,
        elementId,
        uploadedBy,
        uploadedAt,
        createdAt,
        updatedAt);
  }
}
