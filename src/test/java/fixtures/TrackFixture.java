package fixtures;

import fr.avenirsesr.portfolio.api.domain.model.AMS;
import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.api.domain.model.Track;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.FakeTrack;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TrackFixture {

  private UUID id;
  private User user;
  private String title;
  private List<SkillLevel> skillLevels;
  private List<AMS> amses;
  private Instant createdAt;
  private boolean isGroup;

  private TrackFixture() {
    var fakeUser = UserFixture.create().toModel();
    var base = FakeTrack.of(fakeUser).toModel();
    this.id = base.getId();
    this.user = base.getUser();
    this.title = base.getTitle();
    this.skillLevels = base.getSkillLevels();
    this.amses = base.getAmses();
    this.createdAt = base.getCreatedAt();
    this.isGroup = base.isGroup();
  }

  public static TrackFixture create() {
    return new TrackFixture();
  }

  public TrackFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public TrackFixture withUser(User user) {
    this.user = user;
    return this;
  }

  public TrackFixture withTitle(String title) {
    this.title = title;
    return this;
  }

  public TrackFixture withSkillLevels(List<SkillLevel> skillLevels) {
    this.skillLevels = skillLevels;
    return this;
  }

  public TrackFixture withSkillLevels(int count) {
    this.skillLevels = SkillLevelFixture.create().withCount(count);
    return this;
  }

  public TrackFixture withAmses(List<AMS> amses) {
    this.amses = amses;
    return this;
  }

  public TrackFixture withAmses(int count) {
    this.amses = AMSFixture.create().withCount(count);
    return this;
  }

  public TrackFixture withCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
    return this;
  }

  public TrackFixture withGroup(boolean isGroup) {
    this.isGroup = isGroup;
    return this;
  }

  public List<Track> withCount(int count) {
    List<Track> tracks = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      tracks.add(create().toModel());
    }
    return tracks;
  }

  public Track toModel() {
    return Track.toDomain(id, user, title, skillLevels, amses, isGroup, createdAt);
  }
}
