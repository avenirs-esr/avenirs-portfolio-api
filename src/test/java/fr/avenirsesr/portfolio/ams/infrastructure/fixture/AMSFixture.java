package fr.avenirsesr.portfolio.ams.infrastructure.fixture;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.ams.domain.model.Cohort;
import fr.avenirsesr.portfolio.ams.domain.model.enums.EAmsStatus;
import fr.avenirsesr.portfolio.ams.infrastructure.adapter.seeder.fake.FakeAMS;
import fr.avenirsesr.portfolio.student.progress.infrastructure.fixture.SkillLevelFixture;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.student.progress.infrastructure.adapter.mapper.SkillLevelMapper;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.user.domain.model.User;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.UserMapper;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
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
    var base = FakeAMS.of(UserMapper.fromDomain(fakeUser)).toEntity();
    this.id = base.getId();
    this.user = fakeUser;
    this.title = "fake ams title";
    this.skillLevels =
        base.getSkillLevels().stream().map(SkillLevelMapper::toDomainWithoutRecursion).toList();
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

  public AMSFixture withStartDate(Instant startDate) {
    this.startDate = startDate;
    return this;
  }

  public AMSFixture withEndDate(Instant endDate) {
    this.endDate = endDate;
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
        EAmsStatus.NOT_STARTED);
  }
}
