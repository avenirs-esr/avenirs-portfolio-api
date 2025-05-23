package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.api.domain.model.AMS;
import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.api.domain.model.Track;
import fr.avenirsesr.portfolio.api.domain.model.User;
import java.util.List;
import java.util.stream.Stream;

public class FakeTrack {
  private final Track track;

  private FakeTrack(Track track) {
    this.track = track;
  }

  public static FakeTrack of(User user) {
    return new FakeTrack(Track.create(user));
  }

  public FakeTrack withSkillLevel(List<SkillLevel> skillLevels) {
    track.setSkillLevels(skillLevels);
    skillLevels.forEach(
        skillLevel ->
            skillLevel.setTracks(
                Stream.concat(skillLevel.getTracks().stream(), Stream.of(track)).toList()));
    return this;
  }

  public FakeTrack withAMS(List<AMS> amses) {
    track.setAmses(amses);
    return this;
  }

  public Track toModel() {
    return track;
  }
}
