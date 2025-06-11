package fr.avenirsesr.portfolio.api.application.adapter.controller;

import fr.avenirsesr.portfolio.api.application.adapter.dto.NavigationAccessDTO;
import fr.avenirsesr.portfolio.api.domain.exception.UserIsNotStudentException;
import fr.avenirsesr.portfolio.api.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.api.domain.port.input.InstitutionService;
import fr.avenirsesr.portfolio.api.domain.port.input.ProgramProgressService;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.UserRepository;
import java.security.Principal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/me/navigation-access")
public class NavigationAccessController {
  private final UserRepository userRepository;
  private final InstitutionService institutionService;
  private final ProgramProgressService programProgressService;

  @GetMapping
  public ResponseEntity<NavigationAccessDTO> getStudentNavigationAccess(
      Principal principal,
      @RequestHeader(value = "Accept-Language", defaultValue = "fr") String lang) {
    ELanguage language = ELanguage.fromCode(lang);
    var userId = UUID.fromString(principal.getName());

    log.info("Received request to get navigation access of student [{}]", userId);

    var user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

    if (!user.isStudent()) {
      log.error("User {} is not a student", userId);
      throw new UserIsNotStudentException();
    }

    var student = user.toStudent();

    var isAPCEnabledByInstitution =
        institutionService.isNavigationEnabledFor(student, EPortfolioType.APC, language);
    var isLifeProjectEnabledByInstitution =
        institutionService.isNavigationEnabledFor(student, EPortfolioType.LIFE_PROJECT, language);

    var isFollowingAPCProgram = programProgressService.isStudentFollowingAPCProgram(student);

    var navigationAccess =
        new NavigationAccessDTO(
            new NavigationAccessDTO.AccessInfoAPC(isAPCEnabledByInstitution, isFollowingAPCProgram),
            new NavigationAccessDTO.AccessInfoLifeProject(isLifeProjectEnabledByInstitution));

    log.debug("Navigation access of student {} : {}", userId, navigationAccess);
    return ResponseEntity.ok(navigationAccess);
  }
}
