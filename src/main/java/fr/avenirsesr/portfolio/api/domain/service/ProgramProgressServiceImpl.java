package fr.avenirsesr.portfolio.api.domain.service;

import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.port.input.ProgramProgressService;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.ProgramProgressRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class ProgramProgressServiceImpl implements ProgramProgressService {

  private final ProgramProgressRepository programProgressRepository;

  @Override
  public boolean isStudentFollowingAPCProgram(Student student) {
    var programProgress =
        programProgressRepository.findAllAPCByStudent(student);
    return !programProgress.isEmpty();
  }
}
