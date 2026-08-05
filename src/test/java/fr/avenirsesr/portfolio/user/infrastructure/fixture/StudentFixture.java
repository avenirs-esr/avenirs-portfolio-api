package fr.avenirsesr.portfolio.user.infrastructure.fixture;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.student.selfknowledge.domain.model.enums.ESelfKnowledgeCategory;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StudentFixture {
  private UUID id;
  private String bio;
  private User user;
  private UUID institutionId;
  private UUID groupId;
  private File profilePicture;
  private File coverPicture;
  private boolean hasUnseenNotification;
  private List<ESelfKnowledgeCategory> selfKnowledgeCategories;
  private Instant createdAt;
  private Instant updatedAt;

  private StudentFixture() {
    this.user = UserFixture.create().toModel();
    this.id = user.getId();
    this.bio = "this is my student bio";
    this.hasUnseenNotification = false;
    this.selfKnowledgeCategories = new ArrayList<>();
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
  }

  public static StudentFixture create() {
    return new StudentFixture();
  }

  public StudentFixture withId(UUID id) {
    this.id = id;
    this.user = UserFixture.create().withId(id).toModel();
    return this;
  }

  public StudentFixture withBio(String bio) {
    this.bio = bio;
    return this;
  }

  public StudentFixture withUser(User user) {
    this.user = user;
    return this;
  }

  public StudentFixture withCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public StudentFixture withUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public StudentFixture withHasUnseenNotification(boolean hasUnseenNotification) {
    this.hasUnseenNotification = hasUnseenNotification;
    return this;
  }

  public StudentFixture withInstitutionId(UUID institutionId) {
    this.institutionId = institutionId;
    return this;
  }

  public StudentFixture withGroupId(UUID groupId) {
    this.groupId = groupId;
    return this;
  }

  public StudentFixture withSelfKnowledgeCategories(
      List<ESelfKnowledgeCategory> selfKnowledgeCategories) {
    this.selfKnowledgeCategories = selfKnowledgeCategories;
    return this;
  }

  public Student toModel() {
    return Student.toDomain(
        user,
        user.getEmail(),
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
}
