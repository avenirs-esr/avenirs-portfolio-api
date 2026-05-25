package fr.avenirsesr.portfolio.file.infrastructure.adapter.model;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.file.domain.model.EFileType;
import fr.avenirsesr.portfolio.file.domain.model.EUserPhotoType;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "user_photo",
    indexes = {
      @Index(name = "idx_user_photo_user", columnList = "user_id"),
      @Index(
          name = "idx_user_photo_user_type_active",
          columnList = "user_id, user_photo_type, is_active_version"),
      @Index(name = "idx_user_photo_user_version", columnList = "user_id, version DESC")
    })
@NoArgsConstructor
@Getter
@Setter
public class UserPhotoEntity extends AvenirsBaseEntity {
  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @Column(nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, name = "user_category")
  private EUserCategory userCategory;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, name = "user_photo_type")
  private EUserPhotoType userPhotoType;

  @Column(nullable = false, name = "file_type")
  @Enumerated(EnumType.STRING)
  private EFileType fileType;

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

  private UserPhotoEntity(
      UUID id,
      String name,
      UserEntity user,
      EUserCategory userCategory,
      EUserPhotoType userPhotoType,
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
    this.name = name;
    this.user = user;
    this.userCategory = userCategory;
    this.userPhotoType = userPhotoType;
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

  public static UserPhotoEntity of(
      UUID id,
      String name,
      UserEntity user,
      EUserCategory userCategory,
      EUserPhotoType userPhotoType,
      EFileType fileType,
      long size,
      int version,
      boolean isActiveVersion,
      String uri,
      UserEntity uploadedBy,
      Instant uploadedAt,
      Instant createdAt,
      Instant updatedAt) {
    return new UserPhotoEntity(
        id,
        name,
        user,
        userCategory,
        userPhotoType,
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
