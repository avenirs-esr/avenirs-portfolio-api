package fr.avenirsesr.portfolio.api.domain.model;

import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AMS {
  @Setter(AccessLevel.NONE)
  private final UUID id;

  @Setter(AccessLevel.NONE)
  private final User user;

  @Setter(AccessLevel.NONE)
  private final String title;

  private final ELanguage language;

  @Setter(AccessLevel.NONE)
  private final Instant startDate;

  @Setter(AccessLevel.NONE)
  private final Instant endDate;

  private List<SkillLevel> skillLevels;

  private AMS(UUID id, User user, String title, Instant startDate, Instant endDate, ELanguage language) {
    this.id = id;
    this.user = user;
    this.title = title;
    this.startDate = startDate;
    this.endDate = endDate;
    this.language = language;
  }

  public static AMS create(UUID id, User user, String title, Instant startDate, Instant endDate, ELanguage language) {
    var ams = new AMS(id, user, title, startDate, endDate, language);
    ams.setSkillLevels(List.of());

    return ams;
  }

  public static AMS toDomain(
      UUID id, User user, String title, Instant startDate, Instant endDate, List<SkillLevel> skillLevels, ELanguage language) {
    var ams = new AMS(id, user, title, startDate, endDate, language);
    ams.setSkillLevels(skillLevels);

    return ams;
  }
}
