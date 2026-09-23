package fr.avenirsesr.portfolio.student.experience.application.adapter.controller;

import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.ESortField;
import fr.avenirsesr.portfolio.common.data.domain.model.enums.ESortOrder;
import fr.avenirsesr.portfolio.student.experience.application.adapter.dto.DeclaredExperienceRequest;
import fr.avenirsesr.portfolio.student.experience.application.adapter.dto.DeclaredExperienceViewDTO;
import fr.avenirsesr.portfolio.student.experience.application.adapter.mapper.DeclaredExperienceMapper;
import fr.avenirsesr.portfolio.student.experience.domain.data.DeclaredExperienceData;
import fr.avenirsesr.portfolio.student.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.experience.domain.model.enums.EExperienceType;
import fr.avenirsesr.portfolio.student.experience.domain.port.input.DeclaredExperienceService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/me/declared/experiences")
public class DeclaredExperienceController {
  private final DeclaredExperienceService declaredExperienceService;
  private final DeclaredExperienceMapper declaredExperienceMapper;

  @PreAuthorize("hasAuthority('declared-experience:create:own')")
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
            request.result(),
            request.startDate(),
            request.endDate());

    return ResponseEntity.created(URI.create("/me/declared/experiences/" + experience.getId()))
        .body(declaredExperienceMapper.toDTO(experience));
  }

  @PreAuthorize("hasAuthority('declared-experience:list:own')")
  @GetMapping("/{experienceId}")
  public ResponseEntity<DeclaredExperienceViewDTO> getDeclaredExperience(
      @Valid @PathVariable UUID experienceId) {
    DeclaredExperience experience = declaredExperienceService.get(experienceId);

    return ResponseEntity.ok(declaredExperienceMapper.toDTO(experience));
  }

  @PreAuthorize("hasAuthority('declared-experience:list:own')")
  @GetMapping("/view")
  public ResponseEntity<PagedResponse<DeclaredExperienceViewDTO>> getDeclaredExperienceView(
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize,
      @RequestParam(required = false) Boolean isValorized,
      @Parameter(
              array = @ArraySchema(schema = @Schema(ref = "#/components/schemas/EExperienceType")))
          @RequestParam(required = false)
          List<EExperienceType> experienceTypes,
      @Parameter(schema = @Schema(ref = "#/components/schemas/ESortField"))
          @RequestParam(required = false)
          ESortField sortField,
      @Parameter(schema = @Schema(ref = "#/components/schemas/ESortOrder"))
          @RequestParam(required = false)
          ESortOrder sortOrder) {
    SortCriteria sortCriteria =
        (sortField != null && sortOrder != null) ? new SortCriteria(sortField, sortOrder) : null;

    PagedResult<DeclaredExperienceData> pagedExperiences =
        declaredExperienceService.getView(
            new PageCriteria(page, pageSize), isValorized, experienceTypes, sortCriteria);

    return ResponseEntity.ok(
        new PagedResponse<>(
            pagedExperiences.content().stream().map(declaredExperienceMapper::toDTO).toList(),
            PageInfoDTO.fromDomain(pagedExperiences.pageInfo())));
  }

  @PreAuthorize("hasAuthority('declared-experience:update:own')")
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
            request.result(),
            request.startDate(),
            request.endDate(),
            Boolean.TRUE.equals(request.valorized()));

    return ResponseEntity.ok(declaredExperienceMapper.toDTO(experience));
  }

  @PreAuthorize("hasAuthority('declared-experience:delete:own')")
  @DeleteMapping("/")
  public ResponseEntity<String> deleteDeclaredExperiences(@RequestBody List<UUID> experienceIds) {
    declaredExperienceService.delete(experienceIds);
    return ResponseEntity.ok("Declared experiences successfully deleted");
  }
}
