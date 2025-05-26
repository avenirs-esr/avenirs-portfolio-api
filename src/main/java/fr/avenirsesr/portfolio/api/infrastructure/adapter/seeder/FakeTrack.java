package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.api.domain.model.AMS;
import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.api.domain.model.Track;
import fr.avenirsesr.portfolio.api.domain.model.User;
import java.time.Instant;
import java.util.List;
import net.datafaker.Faker;

public class FakeTrack {
  private static final Faker faker = new Faker();
  private final Track track;

  private FakeTrack(Track track) {
    this.track = track;
  }

  public static FakeTrack of(User user) {
    return new FakeTrack(Track.create(user, faker.lorem().sentence(4)));
  }

  public FakeTrack withSkillLevel(List<SkillLevel> skillLevels) {
    track.setSkillLevels(skillLevels);
    return this;
  }

  public FakeTrack withAMS(List<AMS> amses) {
    track.setAmses(amses);
    return this;
  }

  public FakeTrack isGroup() {
    track.setGroup(true);
    return this;
  }

  public FakeTrack withCreatedAt(Instant createdAt) {
    track.setCreatedAt(createdAt);
    return this;
  }

  public Track toModel() {
    return track;
  }
}
