package fr.avenirsesr.portfolio.user.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.file.domain.model.File;
import java.time.Instant;
import java.util.Optional;
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

  @Getter(AccessLevel.NONE)
  private File coverPicture;

  @Getter(AccessLevel.NONE)
  private File profilePicture;

  private Staff(
      User user,
      String institutionEmail,
      String bio,
      File coverPicture,
      File profilePicture,
      Instant createdAt,
      Instant updatedAt) {
    super(user.getId(), createdAt, updatedAt);
    this.user = user;
    this.bio = bio;
    this.institutionEmail = institutionEmail;
    this.coverPicture = coverPicture;
    this.profilePicture = profilePicture;
  }

  public static Staff create(User user, String institutionEmail, String bio) {
    return new Staff(user, institutionEmail, bio, null, null, Instant.now(), Instant.now());
  }

  public static Staff toDomain(
      User user,
      String institutionEmail,
      String bio,
      File coverPicture,
      File profilePicture,
      Instant createdAt,
      Instant updatedAt) {
    return new Staff(
        user, institutionEmail, bio, coverPicture, profilePicture, createdAt, updatedAt);
  }

  public Optional<File> getCoverPicture() {
    return Optional.ofNullable(coverPicture);
  }

  public Optional<File> getProfilePicture() {
    return Optional.ofNullable(profilePicture);
  }
}
