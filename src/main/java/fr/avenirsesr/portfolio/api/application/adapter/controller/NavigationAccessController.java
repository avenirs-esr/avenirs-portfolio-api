package fr.avenirsesr.portfolio.api.application.adapter.controller;

import fr.avenirsesr.portfolio.api.application.adapter.dto.NavigationAccessDTO;
import fr.avenirsesr.portfolio.api.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.api.domain.port.input.InstitutionService;
import fr.avenirsesr.portfolio.api.domain.port.input.ProgramProgressService;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.UserRepository;
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
      @RequestHeader("X-Signed-Context") String userIdRaw) {
    var userId = UUID.fromString(userIdRaw); // @Todo: fetch userLoggedIn

    log.info("Received request to get navigation access of student [{}]", userId);

    var user = userRepository.findById(userId).orElseThrow();

    if (!user.isStudent()) {
      log.error("User {} is not a student", userId);
      throw new RuntimeException(); // todo -> throw not allowed exception
    }

    var student = user.toStudent();

    var isAPCEnabledByInstitution =
        institutionService.isNavigationEnabledFor(student, EPortfolioType.APC);
    var isLifeProjectEnabledByInstitution =
        institutionService.isNavigationEnabledFor(student, EPortfolioType.LIFE_PROJECT);

    var isFollowingAPCProgram = programProgressService.isStudentFollowingAPCProgram(student);

    var navigationAccess =
        new NavigationAccessDTO(
            new NavigationAccessDTO.AccessInfoAPC(isAPCEnabledByInstitution, isFollowingAPCProgram),
            new NavigationAccessDTO.AccessInfoLifeProject(isLifeProjectEnabledByInstitution));

    log.debug("Navigation access of student {} : {}", userId, navigationAccess);
    return ResponseEntity.ok(navigationAccess);
  }
}
