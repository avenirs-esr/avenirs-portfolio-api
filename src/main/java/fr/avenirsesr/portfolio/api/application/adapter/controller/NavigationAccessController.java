package fr.avenirsesr.portfolio.api.application.adapter.controller;

import fr.avenirsesr.portfolio.api.application.adapter.dto.NavigationAccessDTO;
import fr.avenirsesr.portfolio.api.application.adapter.util.UserUtil;
import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.domain.model.enums.EPortfolioType;
import fr.avenirsesr.portfolio.api.domain.port.input.InstitutionService;
import fr.avenirsesr.portfolio.api.domain.port.input.ProgramProgressService;
import java.security.Principal;
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
  private final UserUtil userUtil;
  private final InstitutionService institutionService;
  private final ProgramProgressService programProgressService;

  @GetMapping
  public ResponseEntity<NavigationAccessDTO> getStudentNavigationAccess(
      Principal principal,
      @RequestHeader(value = "Accept-Language", defaultValue = "fr_FR") String lang) {
    ELanguage language = ELanguage.fromCode(lang);
    Student student = userUtil.getStudent(principal);

    var isAPCEnabledByInstitution =
        institutionService.isNavigationEnabledFor(student, EPortfolioType.APC, language);
    var isLifeProjectEnabledByInstitution =
        institutionService.isNavigationEnabledFor(student, EPortfolioType.LIFE_PROJECT, language);

    var isFollowingAPCProgram = programProgressService.isStudentFollowingAPCProgram(student);

    var navigationAccess =
        new NavigationAccessDTO(
            new NavigationAccessDTO.AccessInfoAPC(isAPCEnabledByInstitution, isFollowingAPCProgram),
            new NavigationAccessDTO.AccessInfoLifeProject(isLifeProjectEnabledByInstitution));

    return ResponseEntity.ok(navigationAccess);
  }
}
