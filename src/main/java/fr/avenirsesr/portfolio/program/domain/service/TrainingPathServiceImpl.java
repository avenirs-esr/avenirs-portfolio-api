package fr.avenirsesr.portfolio.program.domain.service;

import fr.avenirsesr.portfolio.common.error.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.common.web.infrastructure.context.RequestContext;
import fr.avenirsesr.portfolio.program.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.program.domain.port.input.TrainingPathService;
import fr.avenirsesr.portfolio.program.domain.port.output.repository.TrainingPathRepository;
import fr.avenirsesr.portfolio.user.domain.exception.UserIsNotStudentException;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StudentRepository;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class TrainingPathServiceImpl implements TrainingPathService {
  private final StudentRepository studentRepository;
  private final TrainingPathRepository trainingPathRepository;

  @Override
  public List<TrainingPath> getTrainingPathsByStudent() {
    var loggedInUser = RequestContext.get().userLoggedIn().orElseThrow(UserNotFoundException::new);
    var student =
        studentRepository
            .findById(loggedInUser.getId())
            .orElseThrow(UserIsNotStudentException::new);
    return trainingPathRepository.findAllTrainingPathsByStudents(student).stream()
        .sorted(Comparator.comparing(p -> p.getProgram().getName()))
        .collect(Collectors.toList());
  }
}
