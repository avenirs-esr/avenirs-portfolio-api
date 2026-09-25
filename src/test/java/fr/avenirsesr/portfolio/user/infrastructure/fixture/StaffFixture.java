package fr.avenirsesr.portfolio.user.infrastructure.fixture;

import fr.avenirsesr.portfolio.common.data.domain.model.User;
import fr.avenirsesr.portfolio.file.domain.model.File;
import fr.avenirsesr.portfolio.user.domain.model.Staff;
import java.time.Instant;
import java.util.UUID;

public class StaffFixture {
  private UUID id;
  private String bio;
  private User user;
  private File profilePicture;
  private File coverPicture;
  private boolean hasUnseenNotification;
  private Instant createdAt;
  private Instant updatedAt;

  private StaffFixture() {
    this.user = UserFixture.create().toModel();
    this.id = user.getId();
    this.bio = "this is my staff bio";
    this.hasUnseenNotification = false;
    this.createdAt = Instant.now();
    this.updatedAt = Instant.now();
  }

  public static StaffFixture create() {
    return new StaffFixture();
  }

  public StaffFixture withId(UUID id) {
    this.id = id;
    this.user = UserFixture.create().withId(id).toModel();
    return this;
  }

  public StaffFixture withBio(String bio) {
    this.bio = bio;
    return this;
  }

  public StaffFixture withUser(User user) {
    this.user = user;
    return this;
  }

  public StaffFixture withCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public StaffFixture withUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
    return this;
  }

  public StaffFixture withHasUnseenNotification(boolean hasUnseenNotification) {
    this.hasUnseenNotification = hasUnseenNotification;
    return this;
  }

  public Staff toModel() {
    return Staff.toDomain(
        user,
        user.getEmail(),
        bio,
        hasUnseenNotification,
        coverPicture,
        profilePicture,
        createdAt,
        updatedAt);
  }
}
