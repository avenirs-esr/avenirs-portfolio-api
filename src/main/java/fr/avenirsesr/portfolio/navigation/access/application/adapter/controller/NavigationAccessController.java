package fr.avenirsesr.portfolio.navigation.access.application.adapter.controller;

import fr.avenirsesr.portfolio.navigation.access.application.adapter.dto.NavigationAccessDTO;
import fr.avenirsesr.portfolio.program.domain.port.input.InstitutionService;
import fr.avenirsesr.portfolio.shared.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.student.progress.domain.port.input.StudentProgressService;
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
  private final InstitutionService institutionService;
  private final StudentProgressService studentProgressService;

  @GetMapping
  public ResponseEntity<NavigationAccessDTO> getStudentNavigationAccess() {
    var isAPCEnabledByInstitution = institutionService.isNavigationEnabledFor(EPortfolioType.APC);
    var isLifeProjectEnabledByInstitution =
        institutionService.isNavigationEnabledFor(EPortfolioType.LIFE_PROJECT);

    var isFollowingAPCProgram = studentProgressService.isStudentFollowingAPCProgram();

    var navigationAccess =
        new NavigationAccessDTO(
            new NavigationAccessDTO.AccessInfoAPC(isAPCEnabledByInstitution, isFollowingAPCProgram),
            new NavigationAccessDTO.AccessInfoLifeProject(isLifeProjectEnabledByInstitution));

    return ResponseEntity.ok(navigationAccess);
  }
}
