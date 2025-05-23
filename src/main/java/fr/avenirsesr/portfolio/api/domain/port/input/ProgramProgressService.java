package fr.avenirsesr.portfolio.api.domain.port.input;

import fr.avenirsesr.portfolio.api.domain.model.Student;

public interface ProgramProgressService {
  boolean isStudentFollowingAPCProgram(Student student);
  List<ProgramProgress> getSkillsOverview(UUID userId);
}
