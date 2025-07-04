package fr.avenirsesr.portfolio.student.progress.infrastructure.fixture;

import fr.avenirsesr.portfolio.student.progress.domain.model.Program;
import fr.avenirsesr.portfolio.student.progress.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.student.progress.domain.model.Skill;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.infrastructure.fixture.UserFixture;
import java.util.*;

public class ProgramProgressFixture {

  private UUID id;
  private Program program;
  private Student student;
  private Set<Skill> skills;

  private ProgramProgressFixture() {
    this.id = UUID.randomUUID();
    this.program = ProgramFixture.create().toModel();
    this.student = UserFixture.create().asStudent("Biography", null, null).toModel().toStudent();
    this.skills = new LinkedHashSet<>();
  }

  public static ProgramProgressFixture create() {
    return new ProgramProgressFixture();
  }

  public static ProgramProgressFixture createWithAPC() {
    return new ProgramProgressFixture().withProgram(ProgramFixture.createWithAPC().toModel());
  }

  public static ProgramProgressFixture createWithoutAPC() {
    return new ProgramProgressFixture().withProgram(ProgramFixture.createWithoutAPC().toModel());
  }

  public static ProgramProgressFixture createWithSkillsAndSkillLevels(
      int skillCount, int skillLevelCountBySkill) {
    List<Skill> skills = new ArrayList<>();
    for (int i = 0; i < skillCount; i++) {
      skills.add(SkillFixture.create().withSkillLevels(skillLevelCountBySkill).toModel());
    }
    return new ProgramProgressFixture().withSkills(new HashSet<>(skills));
  }

  public ProgramProgressFixture withId(UUID id) {
    this.id = id;
    return this;
  }

  public ProgramProgressFixture withProgram(Program program) {
    this.program = program;
    return this;
  }

  public ProgramProgressFixture withStudent(Student student) {
    this.student = student;
    return this;
  }

  public ProgramProgressFixture withSkills(Set<Skill> skills) {
    this.skills = skills;
    return this;
  }

  public ProgramProgressFixture withSkills(int count) {
    this.skills = new HashSet<>(SkillFixture.create().withCount(count));
    return this;
  }

  public TrainingPath toModel() {
    return TrainingPath.toDomain(id, program, student, skills);
  }
}
