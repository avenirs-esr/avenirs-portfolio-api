package fr.avenirsesr.portfolio.api.domain.model;

import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
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

  private List<SkillLevel> skillLevels;

  private AMS(UUID id, User user, String title, ELanguage language) {
    this.id = id;
    this.user = user;
    this.title = title;
    this.language = language;
  }

  public static AMS create(UUID id, User user, String title, ELanguage language) {
    var ams = new AMS(id, user, title, language);
    ams.setSkillLevels(List.of());

    return ams;
  }

  public static AMS toDomain(
      UUID id, User user, String title, List<SkillLevel> skillLevels, ELanguage language) {
    var ams = new AMS(id, user, title, language);
    ams.setSkillLevels(skillLevels);

    return ams;
  }
}
