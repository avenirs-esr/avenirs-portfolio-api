package fr.avenirsesr.portfolio.program.domain.service;

import fr.avenirsesr.portfolio.common.error.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.program.domain.port.input.InstitutionService;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.student.progress.domain.port.output.repository.StudentProgressRepository;
import fr.avenirsesr.portfolio.user.domain.exception.UserIsNotStudentException;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class InstitutionServiceImpl implements InstitutionService {
  private final StudentRepository studentRepository;
  private final StudentProgressRepository studentProgressRepository;

  @Override
  public boolean isNavigationEnabledFor(EPortfolioType navigationField) {
    var loggedInUser = RequestContext.get().userLoggedIn().orElseThrow(UserNotFoundException::new);
    var student =
        studentRepository
            .findById(loggedInUser.getId())
            .orElseThrow(UserIsNotStudentException::new);
    var studentProgresses = studentProgressRepository.findAllByStudent(student);

    return studentProgresses.stream()
        .map(studentProgress -> studentProgress.getTrainingPath().getProgram().getInstitution())
        .anyMatch(institution -> institution.getEnabledFields().contains(navigationField));
  }
}
