package fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.controller;

import fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.dto.DeclaredExperienceRequest;
import fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.dto.DeclaredExperienceViewDTO;
import fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.mapper.DeclaredExperienceMapper;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.port.input.DeclaredExperienceService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/me/declared/experiences")
public class DeclaredExperienceController {
  private final DeclaredExperienceService declaredExperienceService;

  @PostMapping("/")
  public ResponseEntity<DeclaredExperienceViewDTO> createDeclaredExperience(
      @Valid @RequestBody DeclaredExperienceRequest request) {
    DeclaredExperience experience =
        declaredExperienceService.create(
            request.title(),
            request.experienceType(),
            request.organization(),
            request.activitySector(),
            request.location(),
            request.description(),
            request.sourceOfInformation(),
            request.summary(),
            request.externalLink(),
            request.startDate(),
            request.endDate());

    return ResponseEntity.created(URI.create("/me/declared/experiences/" + experience.getId()))
        .body(DeclaredExperienceMapper.toDTO(experience));
  }

  @GetMapping("/{experienceId}")
  public ResponseEntity<DeclaredExperienceViewDTO> createDeclaredExperience(
      @Valid @PathVariable UUID experienceId) {
    DeclaredExperience experience = declaredExperienceService.get(experienceId);

    return ResponseEntity.ok(DeclaredExperienceMapper.toDTO(experience));
  }
}
