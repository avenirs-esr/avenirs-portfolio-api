package fr.avenirsesr.portfolio.user.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.file.domain.model.File;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Staff extends AvenirsBaseModel {
  @Setter(AccessLevel.NONE)
  private final User user;

  private String bio;
  private String institutionEmail;
  private List<UUID> institutionIds;
  private List<UUID> groupIds;
  private boolean hasUnseenNotification;

  @Getter(AccessLevel.NONE)
  private File coverPicture;

  @Getter(AccessLevel.NONE)
  private File profilePicture;

  private Staff(
      User user,
      String institutionEmail,
      List<UUID> institutionIds,
      List<UUID> groupIds,
      String bio,
      boolean hasUnseenNotification,
      File coverPicture,
      File profilePicture,
      Instant createdAt,
      Instant updatedAt) {
    super(user.getId(), createdAt, updatedAt);
    this.user = user;
    this.bio = bio;
    this.institutionEmail = institutionEmail;
    this.institutionIds = institutionIds;
    this.groupIds = groupIds;
    this.hasUnseenNotification = hasUnseenNotification;
    this.coverPicture = coverPicture;
    this.profilePicture = profilePicture;
  }

  public static Staff create(
      User user,
      String institutionEmail,
      List<UUID> institutionIds,
      List<UUID> groupIds,
      String bio) {
    return new Staff(
        user,
        institutionEmail,
        institutionIds,
        groupIds,
        bio,
        false,
        null,
        null,
        Instant.now(),
        Instant.now());
  }

  public static Staff toDomain(
      User user,
      String institutionEmail,
      List<UUID> institutionIds,
      List<UUID> groupIds,
      String bio,
      boolean hasUnseenNotification,
      File coverPicture,
      File profilePicture,
      Instant createdAt,
      Instant updatedAt) {
    return new Staff(
        user,
        institutionEmail,
        institutionIds,
        groupIds,
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
