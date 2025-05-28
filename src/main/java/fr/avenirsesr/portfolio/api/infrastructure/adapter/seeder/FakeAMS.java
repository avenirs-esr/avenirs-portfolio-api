package fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder;

import fr.avenirsesr.portfolio.api.domain.model.AMS;
import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.api.domain.model.User;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class FakeAMS {
  private static final FakerProvider faker = new FakerProvider();
  private final AMS ams;

  private FakeAMS(AMS ams) {
    this.ams = ams;
  }

  public static FakeAMS of(User user) {
    return new FakeAMS(AMS.create(UUID.fromString(faker.call().internet().uuid()), user));
  }

  public FakeAMS withSkillLevel(List<SkillLevel> skillLevels) {
    ams.setSkillLevels(skillLevels);
    skillLevels.forEach(
        skillLevel ->
            skillLevel.setAmses(
                Stream.concat(skillLevel.getAmses().stream(), Stream.of(ams)).toList()));
    return this;
  }

  public AMS toModel() {
    return ams;
  }
}
