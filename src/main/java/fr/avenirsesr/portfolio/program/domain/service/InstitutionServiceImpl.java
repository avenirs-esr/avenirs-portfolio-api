package fr.avenirsesr.portfolio.program.domain.service;

import fr.avenirsesr.portfolio.program.domain.port.input.InstitutionService;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.StudentProgressRepository;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class InstitutionServiceImpl implements InstitutionService {
  private final StudentProgressRepository studentProgressRepository;
  private final LoggedInUserService loggedInUserService;

  @Override
  public boolean isNavigationEnabledFor(EPortfolioType navigationField) {
    Student student = loggedInUserService.getLoggedInStudent();
    var studentProgresses = studentProgressRepository.findAllByStudent(student);

    return studentProgresses.stream()
        .map(studentProgress -> studentProgress.getTrainingPath().getProgram().getInstitution())
        .anyMatch(institution -> institution.getEnabledFields().contains(navigationField));
  }
}
