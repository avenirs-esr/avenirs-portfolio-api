package fr.avenirsesr.portfolio.program.domain.service;

import fr.avenirsesr.portfolio.program.domain.model.TrainingPath;
import fr.avenirsesr.portfolio.program.domain.port.input.TrainingPathService;
import fr.avenirsesr.portfolio.program.domain.port.output.TrainingPathRepository;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class TrainingPathServiceImpl implements TrainingPathService {
  private final TrainingPathRepository trainingPathRepository;

  @Override
  public List<TrainingPath> getTrainingPathsByStudent(Student student) {
    return trainingPathRepository.findAllTrainingPathsByStudents(student).stream()
        .sorted(Comparator.comparing(p -> p.getProgram().getName()))
        .collect(Collectors.toList());
  }
}
