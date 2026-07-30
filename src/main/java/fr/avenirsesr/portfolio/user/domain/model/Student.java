package fr.avenirsesr.portfolio.user.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.file.domain.model.File;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Student extends AvenirsBaseModel {
  @Setter(AccessLevel.NONE)
  private final User user;

  @Setter(AccessLevel.NONE)
  private String institutionEmail;

  private UUID institutionId;

  private UUID groupId;

  private String bio;

  private boolean hasUnseenNotification;

  @Getter(AccessLevel.NONE)
  private File coverPicture;

  @Getter(AccessLevel.NONE)
  private File profilePicture;

  private Student(
      User user,
      String institutionEmail,
      UUID institutionId,
      UUID groupId,
      String bio,
      boolean hasUnseenNotification,
      File coverPicture,
      File profilePicture,
      Instant createdAt,
      Instant updatedAt) {
    super(user.getId(), createdAt, updatedAt);
    this.user = user;
    this.institutionEmail = institutionEmail;
    this.institutionId = institutionId;
    this.groupId = groupId;
    this.bio = bio;
    this.hasUnseenNotification = hasUnseenNotification;
    this.coverPicture = coverPicture;
    this.profilePicture = profilePicture;
  }

  public static Student create(
      User user, String institutionEmail, UUID institutionId, UUID groupId, String bio) {
    return new Student(
        user,
        institutionEmail,
        institutionId,
        groupId,
        bio,
        false,
        null,
        null,
        Instant.now(),
        Instant.now());
  }

  public static Student toDomain(
      User user,
      String institutionEmail,
      UUID institutionId,
      UUID groupId,
      String bio,
      boolean hasUnseenNotification,
      File coverPicture,
      File profilePicture,
      Instant createdAt,
      Instant updatedAt) {
    return new Student(
        user,
        institutionEmail,
        institutionId,
        groupId,
        bio,
        hasUnseenNotification,
        coverPicture,
        profilePicture,
        createdAt,
        updatedAt);
  }

  public Optional<File> getCoverPicture() {
    return Optional.ofNullable(coverPicture);
  }

  public Optional<File> getProfilePicture() {
    return Optional.ofNullable(profilePicture);
  }
}
