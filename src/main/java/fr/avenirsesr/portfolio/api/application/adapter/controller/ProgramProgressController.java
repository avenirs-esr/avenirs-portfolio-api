package fr.avenirsesr.portfolio.api.application.adapter.controller;

import fr.avenirsesr.portfolio.api.application.adapter.dto.ProgramProgressDTO;
import fr.avenirsesr.portfolio.api.application.adapter.mapper.ProgramProgressMapper;
import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.port.input.ProgramProgressService;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.UserRepository;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/program-progress")
public class ProgramProgressController {
  private final ProgramProgressService programProgressService;
  private final UserRepository userRepository;

  @GetMapping("/skills/overview")
  public List<ProgramProgressDTO> getSkillsOverview(
      @RequestHeader("X-Signed-Context") String userId) {
    User user =
        userRepository
            .findById(UUID.fromString(userId))
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    if (!user.isStudent()) {
      log.error("User {} is not a student", userId);
      throw new RuntimeException(); // todo -> throw not allowed exception
    }

    Student student = user.toStudent();
    return programProgressService.getSkillsOverview(student).entrySet().stream()
        .map(entry -> ProgramProgressMapper.fromDomainToDto(entry.getKey(), entry.getValue()))
        .toList();
  }
}
