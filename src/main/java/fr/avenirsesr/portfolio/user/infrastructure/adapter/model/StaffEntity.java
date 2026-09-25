package fr.avenirsesr.portfolio.user.infrastructure.adapter.model;

import static fr.avenirsesr.portfolio.common.validation.domain.constraints.FieldMaxLengths.BIO_LENGTH;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.model.AvenirsBaseEntity;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.model.FileEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "staff")
@NoArgsConstructor
@Getter
@Setter
public class StaffEntity extends AvenirsBaseEntity {
  @OneToOne private UserEntity user;

  @Column(length = BIO_LENGTH)
  private String bio;

  @Email
  @Column(nullable = false, name = "institution_email")
  private String institutionEmail;

  @Column(name = "has_unseen_notification", nullable = false)
  private boolean hasUnseenNotification;

  @OneToOne
  @JoinColumn(name = "cover_picture_id")
  private FileEntity coverPicture;

  @OneToOne
  @JoinColumn(name = "profile_picture_id")
  private FileEntity profilePicture;

  private StaffEntity(
      UUID id,
      UserEntity user,
      String institutionEmail,
      String bio,
      boolean hasUnseenNotification,
      FileEntity coverPicture,
      FileEntity profilePicture,
      Instant createdAt,
      Instant updatedAt) {
    setId(id);
    this.user = user;
    this.bio = bio;
    this.institutionEmail = institutionEmail;
    this.hasUnseenNotification = hasUnseenNotification;
    this.coverPicture = coverPicture;
    this.profilePicture = profilePicture;
    setCreatedAt(createdAt);
    setUpdatedAt(updatedAt);
  }

  public static StaffEntity of(
      UserEntity user,
      String institutionEmail,
      String bio,
      boolean hasUnseenNotification,
      FileEntity coverPicture,
      FileEntity profilePicture,
      Instant createdAt,
      Instant updatedAt) {
    return new StaffEntity(
        user.getId(),
        user,
        institutionEmail,
        bio,
        hasUnseenNotification,
        coverPicture,
        profilePicture,
        createdAt,
        updatedAt);
  }
}
