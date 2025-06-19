package fixtures;

import fr.avenirsesr.portfolio.api.domain.model.*;
import fr.avenirsesr.portfolio.api.domain.model.enums.EAmsStatus;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.FakeAMS;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class AMSFixture {

  private UUID id;
  private User user;
  private String title;
  private List<SkillLevel> skillLevels;
  private ELanguage language = ELanguage.FRENCH;
  private Instant startDate;
  private Instant endDate;

  private AMSFixture() {
    var fakeUser = UserFixture.create().toModel();
    var base = FakeAMS.of(fakeUser).toModel();
    this.id = base.getId();
    this.user = base.getUser();
    this.title = base.getTitle();
    this.skillLevels = base.getSkillLevels();
  }

  public static AMSFixture create() {
    return new AMSFixture();
  }

  public AMSFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public AMSFixture withUser(User user) {
    this.user = user;
    return this;
  }

  public AMSFixture withSkillLevels(List<SkillLevel> skillLevels) {
    this.skillLevels = skillLevels;
    return this;
  }

  public AMSFixture withSkillLevels(int count) {
    this.skillLevels = SkillLevelFixture.create().withCount(count);
    return this;
  }

  public List<AMS> withCount(int count) {
    List<AMS> amses = new ArrayList<AMS>();
    for (int i = 0; i < count; i++) {
      amses.add(create().toModel());
    }
    return amses;
  }

  public AMSFixture withTitle(String title) {
    this.title = title;
    return this;
  }

  public AMSFixture withLanguage(ELanguage language) {
    this.language = language;
    return this;
  }

  public AMS toModel() {
    return AMS.toDomain(
        id,
        user,
        title,
        startDate,
        endDate,
        skillLevels,
        List.of(),
        new HashSet<Cohort>(),
        language,
        EAmsStatus.NOT_STARTED);
  }
}
