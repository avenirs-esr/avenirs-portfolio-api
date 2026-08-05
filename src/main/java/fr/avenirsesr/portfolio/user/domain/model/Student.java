package fr.avenirsesr.portfolio.user.domain.model;

import fr.avenirsesr.portfolio.common.data.domain.model.AvenirsBaseModel;
import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.student.selfknowledge.domain.model.enums.ESelfKnowledgeCategory;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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

  private List<ESelfKnowledgeCategory> selfKnowledgeCategories;

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
      List<ESelfKnowledgeCategory> selfKnowledgeCategories,
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
    this.selfKnowledgeCategories = selfKnowledgeCategories;
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
        new ArrayList<>(),
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
      List<ESelfKnowledgeCategory> selfKnowledgeCategories,
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
        selfKnowledgeCategories,
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
