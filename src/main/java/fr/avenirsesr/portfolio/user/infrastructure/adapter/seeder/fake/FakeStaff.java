package fr.avenirsesr.portfolio.user.infrastructure.adapter.seeder.fake;

import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.StaffEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.model.UserEntity;
import java.time.Instant;

public class FakeStaff {

  private final StaffEntity staff;

  private FakeStaff(StaffEntity staff) {
    this.staff = staff;
  }

  public static FakeStaff create(UserEntity user) {
    return new FakeStaff(
        StaffEntity.of(
            user, user.getEmail(), "fake bio", null, null, Instant.now(), Instant.now()));
  }

  public FakeStaff withBio(String bio) {
    staff.setBio(bio);
    return this;
  }

  public StaffEntity toEntity() {
    return staff;
  }
}
