package fr.avenirsesr.portfolio.file.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.activity.infrastructure.adapter.model.ActivityEntity;
import fr.avenirsesr.portfolio.file.domain.model.shared.EFileType;
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
public class ActivityBannerEntity extends FileEntity {

  @ManyToOne(optional = false)
  @JoinColumn(name = "activity_id", nullable = false)
  private ActivityEntity activity;

  @Column(nullable = false, name = "file_name")
  private String fileName;

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
      Instant uploadedAt) {
    this.setId(id);
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
      Instant uploadedAt) {
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
        uploadedAt);
  }
}
