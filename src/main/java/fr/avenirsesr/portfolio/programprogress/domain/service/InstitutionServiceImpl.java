package fr.avenirsesr.portfolio.programprogress.domain.service;

import fr.avenirsesr.portfolio.programprogress.domain.port.input.InstitutionService;
import fr.avenirsesr.portfolio.programprogress.domain.port.output.repository.ProgramProgressRepository;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class InstitutionServiceImpl implements InstitutionService {
  private final ProgramProgressRepository programProgressRepository;

  @Override
  public boolean isNavigationEnabledFor(Student student, EPortfolioType navigationField) {
    var programProgresses = programProgressRepository.findAllByStudent(student);

    return programProgresses.stream()
        .map(programProgress -> programProgress.getProgram().getInstitution())
        .anyMatch(institution -> institution.getEnabledFields().contains(navigationField));
  }
}
