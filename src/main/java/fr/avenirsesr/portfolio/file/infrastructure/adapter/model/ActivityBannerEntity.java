package fr.avenirsesr.portfolio.file.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.activity.infrastructure.adapter.model.ActivityEntity;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.file.domain.model.EFileType;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "activity_banner",
    indexes = {
      @Index(name = "idx_activity_banner_activity", columnList = "activity_id"),
      @Index(
          name = "idx_activity_banner_activity_active",
          columnList = "activity_id, is_active_version"),
      @Index(
          name = "idx_activity_banner_activity_version",
          columnList = "activity_id, version DESC")
    })
@NoArgsConstructor
@Getter
@Setter
public class ActivityBannerEntity extends AvenirsBaseEntity {

  @ManyToOne(optional = false)
  @JoinColumn(name = "activity_id", nullable = false)
  private ActivityEntity activity;

  @Column(nullable = false, name = "file_type")
  @Enumerated(EnumType.STRING)
  private EFileType fileType;

  @Column(nullable = false, name = "file_name")
  private String fileName;

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

  private ActivityBannerEntity(
      UUID id,
      ActivityEntity activity,
      String fileName,
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
    this.setCreatedAt(createdAt);
    this.setUpdatedAt(updatedAt);
    this.activity = activity;
    this.fileName = fileName;
    this.setFileType(fileType);
    this.setSize(size);
    this.setVersion(version);
    this.setActiveVersion(isActiveVersion);
    this.setUri(uri);
    this.setUploadedBy(uploadedBy);
    this.setUploadedAt(uploadedAt);
  }

  public static ActivityBannerEntity of(
      UUID id,
      ActivityEntity activity,
      String fileName,
      EFileType fileType,
      long size,
      int version,
      boolean isActiveVersion,
      String uri,
      UserEntity uploadedBy,
      Instant uploadedAt,
      Instant createdAt,
      Instant updatedAt) {
    return new ActivityBannerEntity(
        id,
        activity,
        fileName,
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
