package fr.avenirsesr.portfolio.student.progress.domain.service;

import fr.avenirsesr.portfolio.student.progress.domain.port.input.InstitutionService;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.StudentProgressRepository;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class InstitutionServiceImpl implements InstitutionService {
  private final StudentProgressRepository studentProgressRepository;

  @Override
  public boolean isNavigationEnabledFor(Student student, EPortfolioType navigationField) {
    var programProgresses = studentProgressRepository.findAllByStudent(student);

    return programProgresses.stream()
        .map(programProgress -> programProgress.getProgram().getInstitution())
        .anyMatch(institution -> institution.getEnabledFields().contains(navigationField));
  }
}
