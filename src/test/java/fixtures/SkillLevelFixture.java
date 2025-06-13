package fixtures;

import fr.avenirsesr.portfolio.api.domain.model.AMS;
import fr.avenirsesr.portfolio.api.domain.model.Skill;
import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.api.domain.model.Trace;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.FakeSkillLevel;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SkillLevelFixture {

  private UUID id;
  private String name;
  private String description;
  private ESkillLevelStatus status;
  private List<Trace> traces;
  private List<AMS> amses;
  private Skill skill;
  private ELanguage language = ELanguage.FRENCH;
  private Instant startDate;
  private Instant endDate;

  private SkillLevelFixture() {
    SkillLevel base = FakeSkillLevel.create().toModel();
    this.id = base.getId();
    this.name = base.getName();
    this.description = base.getDescription();
    this.status = base.getStatus();
    this.traces = base.getTraces();
    this.amses = base.getAmses();
    this.skill = base.getSkill();
    this.startDate = base.getStartDate();
    this.endDate = base.getEndDate();
  }

  public static SkillLevelFixture create() {
    return new SkillLevelFixture();
  }

  public SkillLevelFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public SkillLevelFixture withName(String name) {
    this.name = name;
    return this;
  }

  public SkillLevelFixture withStatus(ESkillLevelStatus status) {
    Instant pastStartDate = Instant.now().minus(Duration.ofDays(730));
    Instant pastEndDate = Instant.now().minus(Duration.ofDays(365));
    Instant futureStartDate = Instant.now().plus(Duration.ofDays(365));
    Instant futureEndDate = Instant.now().plus(Duration.ofDays(730));
    this.status = status;
    switch (status) {
      case VALIDATED, FAILED -> {
        this.startDate = pastStartDate;
        this.endDate = pastEndDate;
      }
      case UNDER_ACQUISITION, UNDER_REVIEW -> {
        this.startDate = pastStartDate;
        this.endDate = futureEndDate;
      }
      case TO_BE_EVALUATED, NOT_STARTED -> {
        this.startDate = futureStartDate;
        this.endDate = futureEndDate;
      }
    }
    return this;
  }

  public SkillLevelFixture withTrace(List<Trace> traces) {
    this.traces = traces;
    return this;
  }

  public SkillLevelFixture withAmses(List<AMS> amses) {
    this.amses = amses;
    return this;
  }

  public SkillLevelFixture withSkill(Skill skill) {
    this.skill = skill;
    return this;
  }

  public List<SkillLevel> withCount(int count) {
    List<SkillLevel> skillLevels = new ArrayList<SkillLevel>();
    for (int i = 0; i < count; i++) {
      skillLevels.add(create().toModel());
    }
    return skillLevels;
  }

  public SkillLevelFixture withDescription(String description) {
    this.description = description;
    return this;
  }

  public SkillLevelFixture withLanguage(ELanguage language) {
    this.language = language;
    return this;
  }

  public SkillLevelFixture withStartDate(Instant startDate) {
    this.startDate = startDate;
    return this;
  }

  public SkillLevelFixture withEndDate(Instant endDate) {
    this.endDate = endDate;
    return this;
  }

  public SkillLevel toModel() {
    return SkillLevel.toDomain(
        id, name, description, status, traces, amses, skill, language, startDate, endDate);
  }
}
