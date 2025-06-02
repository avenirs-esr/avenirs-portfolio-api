package fixtures;

import fr.avenirsesr.portfolio.api.domain.model.AMS;
import fr.avenirsesr.portfolio.api.domain.model.Skill;
import fr.avenirsesr.portfolio.api.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.api.domain.model.Track;
import fr.avenirsesr.portfolio.api.domain.model.enums.ESkillLevelStatus;
import fr.avenirsesr.portfolio.api.infrastructure.adapter.seeder.FakeSkillLevel;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SkillLevelFixture {

  private UUID id;
  private String name;
  private ESkillLevelStatus status;
  private List<Track> tracks;
  private List<AMS> amses;
  private Skill skill;

  private SkillLevelFixture() {
    SkillLevel base = FakeSkillLevel.create().toModel();
    this.id = base.getId();
    this.name = base.getName();
    this.status = base.getStatus();
    this.tracks = base.getTracks();
    this.amses = base.getAmses();
    this.skill = base.getSkill();
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
    this.status = status;
    return this;
  }

  public SkillLevelFixture withTracks(List<Track> tracks) {
    this.tracks = tracks;
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

  public SkillLevel toModel() {
    return SkillLevel.toDomain(id, name, status, tracks, amses, skill);
  }
}
