package fr.avenirsesr.portfolio.porgramprogress.infrastructure.fixture;

import fr.avenirsesr.portfolio.ams.domain.model.AMS;
import fr.avenirsesr.portfolio.programprogress.domain.model.Skill;
import fr.avenirsesr.portfolio.programprogress.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.programprogress.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.mapper.SkillLevelMapper;
import fr.avenirsesr.portfolio.programprogress.infrastructure.adapter.seeder.fake.FakeSkillLevel;
import fr.avenirsesr.portfolio.shared.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import java.time.LocalDate;
import java.time.Period;
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
  private LocalDate startDate;
  private LocalDate endDate;

  private SkillLevelFixture() {
    SkillLevel base = SkillLevelMapper.toDomain(FakeSkillLevel.create().toEntity(), null);
    this.id = base.getId();
    this.name = base.getName();
    this.description = base.getDescription().orElse(null);
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
    LocalDate pastStartDate = LocalDate.now().minus(Period.ofYears(2));
    LocalDate pastEndDate = LocalDate.now().minus(Period.ofYears(1));
    LocalDate futureStartDate = LocalDate.now().plus(Period.ofYears(1));
    LocalDate futureEndDate = LocalDate.now().plus(Period.ofYears(2));
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

  public SkillLevelFixture withStartDate(LocalDate startDate) {
    this.startDate = startDate;
    return this;
  }

  public SkillLevelFixture withEndDate(LocalDate endDate) {
    this.endDate = endDate;
    return this;
  }

  public SkillLevel toModel() {
    return SkillLevel.toDomain(
        id, name, description, status, traces, amses, skill, startDate, endDate);
  }
}
