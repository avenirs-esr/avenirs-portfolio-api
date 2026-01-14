package fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.controller;

import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.dto.DeclaredExperienceRequest;
import fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.dto.DeclaredExperienceViewDTO;
import fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.mapper.DeclaredExperienceMapper;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.port.input.DeclaredExperienceService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
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
  public ResponseEntity<DeclaredExperienceViewDTO> getDeclaredExperience(
      @Valid @PathVariable UUID experienceId) {
    DeclaredExperience experience = declaredExperienceService.get(experienceId);

    return ResponseEntity.ok(DeclaredExperienceMapper.toDTO(experience));
  }

  @GetMapping("/view")
  public ResponseEntity<PagedResponse<DeclaredExperienceViewDTO>> getDeclaredExperienceView(
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize) {
    PagedResult<DeclaredExperience> pagedExperiences =
        declaredExperienceService.getView(new PageCriteria(page, pageSize));

    return ResponseEntity.ok(
        new PagedResponse<>(
            pagedExperiences.content().stream().map(DeclaredExperienceMapper::toDTO).toList(),
            PageInfoDTO.fromDomain(pagedExperiences.pageInfo())));
  }

  @PutMapping("/{experienceId}")
  public ResponseEntity<DeclaredExperienceViewDTO> updateDeclaredExperience(
      @Valid @PathVariable UUID experienceId,
      @Valid @RequestBody DeclaredExperienceRequest request) {
    var experience =
        declaredExperienceService.update(
            experienceId,
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

    return ResponseEntity.ok(DeclaredExperienceMapper.toDTO(experience));
  }

  @DeleteMapping("/")
  public ResponseEntity<String> deleteDeclaredExperiences(@RequestBody List<UUID> experienceIds) {
    declaredExperienceService.delete(experienceIds);
    return ResponseEntity.ok("Declared experiences successfully deleted");
  }
}
