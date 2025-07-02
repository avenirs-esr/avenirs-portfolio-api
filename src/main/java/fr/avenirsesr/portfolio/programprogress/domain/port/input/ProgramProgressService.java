package fr.avenirsesr.portfolio.programprogress.domain.port.input;

import fr.avenirsesr.portfolio.programprogress.domain.model.ProgramProgress;
import fr.avenirsesr.portfolio.programprogress.domain.model.Skill;
import fr.avenirsesr.portfolio.shared.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ProgramProgressService {
  boolean isStudentFollowingAPCProgram(Student student);

  Map<ProgramProgress, Set<Skill>> getSkillsOverview(Student student);

  Map<ProgramProgress, Set<Skill>> getSkillsView(Student student, SortCriteria sortCriteria);

  List<ProgramProgress> getAllProgramProgress(Student student);
}
