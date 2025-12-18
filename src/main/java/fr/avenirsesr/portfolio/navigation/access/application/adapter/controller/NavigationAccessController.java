package fr.avenirsesr.portfolio.navigation.access.application.adapter.controller;

import fr.avenirsesr.portfolio.common.configuration.domain.model.InstitutionConfigurationElements;
import fr.avenirsesr.portfolio.navigation.access.application.adapter.dto.NavigationAccessDTO;
import fr.avenirsesr.portfolio.program.domain.port.input.InstitutionService;
import fr.avenirsesr.portfolio.student.progress.imported.domain.port.input.StudentProgressService;
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
    InstitutionConfigurationElements institutionConfig =
        institutionService.getInstitutionConfiguration();
    var isAPCEnabledByInstitution = institutionConfig.apcEnabled();
    var isLifeProjectEnabledByInstitution = institutionConfig.lifeProjectEnabled();

    var isFollowingAPCProgram = studentProgressService.isStudentFollowingAPCProgram();

    var navigationAccess =
        new NavigationAccessDTO(
            new NavigationAccessDTO.AccessInfoAPC(isAPCEnabledByInstitution, isFollowingAPCProgram),
            new NavigationAccessDTO.AccessInfoLifeProject(isLifeProjectEnabledByInstitution));

    return ResponseEntity.ok(navigationAccess);
  }
}
