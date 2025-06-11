package fr.avenirsesr.portfolio.api.application.adapter.controller;

import fr.avenirsesr.portfolio.api.application.adapter.dto.ProgramProgressOverviewDTO;
import fr.avenirsesr.portfolio.api.application.adapter.mapper.ProgramProgressOverviewMapper;
import fr.avenirsesr.portfolio.api.domain.exception.UserIsNotStudentException;
import fr.avenirsesr.portfolio.api.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.model.User;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.domain.port.input.ProgramProgressService;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.UserRepository;
import java.security.Principal;
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
@RequestMapping("/me/program-progress")
public class ProgramProgressController {
  private final ProgramProgressService programProgressService;
  private final UserRepository userRepository;

  @GetMapping("/overview")
  public List<ProgramProgressOverviewDTO> getSkillsOverview(
      Principal principal,
      @RequestHeader(value = "Accept-Language", defaultValue = "fr_FR") String lang) {
    ELanguage language = ELanguage.fromCode(lang);
    User user =
        userRepository
            .findById(UUID.fromString(principal.getName()))
            .orElseThrow(UserNotFoundException::new);

    if (!user.isStudent()) {
      log.error("User {} is not a student", principal.getName());
      throw new UserIsNotStudentException();
    }

    Student student = user.toStudent();
    return programProgressService.getSkillsOverview(student, language).entrySet().stream()
        .map(
            entry ->
                ProgramProgressOverviewMapper.fromDomainToDto(entry.getKey(), entry.getValue()))
        .toList();
  }
}
