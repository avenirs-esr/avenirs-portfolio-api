package fr.avenirsesr.portfolio.program.application.adapter.controller;

import fr.avenirsesr.portfolio.program.application.adapter.dto.TrainingPathDTO;
import fr.avenirsesr.portfolio.program.application.adapter.mapper.TrainingPathMapper;
import fr.avenirsesr.portfolio.program.domain.port.input.TrainingPathService;
import fr.avenirsesr.portfolio.shared.application.adapter.utils.UserUtil;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.security.Principal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/me/training-paths")
public class TrainingPathController {
  private final TrainingPathService trainingPathService;
  private final UserUtil userUtil;

  @GetMapping()
  public List<TrainingPathDTO> getAllTrainingPaths(Principal principal) {
    Student student = userUtil.getStudent(principal);
    return trainingPathService.getTrainingPathsByStudent(student).stream()
        .map(TrainingPathMapper::fromDomainToDto)
        .toList();
  }
}
