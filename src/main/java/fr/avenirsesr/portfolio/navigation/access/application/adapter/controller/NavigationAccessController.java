package fr.avenirsesr.portfolio.navigation.access.application.adapter.controller;

import fr.avenirsesr.portfolio.navigation.access.application.adapter.dto.NavigationAccessDTO;
import fr.avenirsesr.portfolio.student.progress.domain.port.input.InstitutionService;
import fr.avenirsesr.portfolio.student.progress.domain.port.input.StudentProgressService;
import fr.avenirsesr.portfolio.shared.application.adapter.utils.UserUtil;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.security.Principal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/me/navigation-access")
public class NavigationAccessController {
  private final UserUtil userUtil;
  private final InstitutionService institutionService;
  private final StudentProgressService programProgressService;

  @GetMapping
  public ResponseEntity<NavigationAccessDTO> getStudentNavigationAccess(Principal principal) {
    Student student = userUtil.getStudent(principal);

    var isAPCEnabledByInstitution =
        institutionService.isNavigationEnabledFor(student, EPortfolioType.APC);
    var isLifeProjectEnabledByInstitution =
        institutionService.isNavigationEnabledFor(student, EPortfolioType.LIFE_PROJECT);

    var isFollowingAPCProgram = programProgressService.isStudentFollowingAPCProgram(student);

    var navigationAccess =
        new NavigationAccessDTO(
            new NavigationAccessDTO.AccessInfoAPC(isAPCEnabledByInstitution, isFollowingAPCProgram),
            new NavigationAccessDTO.AccessInfoLifeProject(isLifeProjectEnabledByInstitution));

    return ResponseEntity.ok(navigationAccess);
  }
}
