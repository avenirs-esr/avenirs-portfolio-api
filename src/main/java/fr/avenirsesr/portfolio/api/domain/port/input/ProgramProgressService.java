package fr.avenirsesr.portfolio.api.domain.port.input;

import fr.avenirsesr.portfolio.api.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.api.domain.model.Skill;
import fr.avenirsesr.portfolio.api.domain.model.Student;
import java.util.Map;
import java.util.Set;

public interface ProgramProgressService {
  boolean isStudentFollowingAPCProgram(Student student);

  Map<ProgramProgress, Set<Skill>> getSkillsOverview(Student student);
}
