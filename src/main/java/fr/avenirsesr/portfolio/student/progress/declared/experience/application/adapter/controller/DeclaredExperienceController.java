package fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.controller;

import fr.avenirsesr.portfolio.association.application.adapter.dto.AssociationSearchResultDeclaredExperienceDTO;
import fr.avenirsesr.portfolio.association.application.adapter.mapper.AssociationSearchResultDTOMapper;
import fr.avenirsesr.portfolio.association.domain.data.AssociationSearchResultData;
import fr.avenirsesr.portfolio.association.domain.model.EAssociationContextType;
import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.shared.application.adapter.dto.AssociationsCreationRequest;
import fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.dto.DeclaredExperienceAssociationsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.dto.DeclaredExperienceRequest;
import fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.dto.DeclaredExperienceViewDTO;
import fr.avenirsesr.portfolio.student.progress.declared.experience.application.adapter.mapper.DeclaredExperienceMapper;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.model.DeclaredExperience;
import fr.avenirsesr.portfolio.student.progress.declared.experience.domain.port.input.DeclaredExperienceService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.net.URI;
import java.security.Principal;
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
  private final DeclaredExperienceMapper declaredExperienceMapper;
  private final AssociationSearchResultDTOMapper associationSearchResultDTOMapper;

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
        .body(declaredExperienceMapper.toDTO(experience));
  }

  @GetMapping("/{experienceId}")
  public ResponseEntity<DeclaredExperienceViewDTO> getDeclaredExperience(
      @Valid @PathVariable UUID experienceId) {
    DeclaredExperience experience = declaredExperienceService.get(experienceId);

    return ResponseEntity.ok(declaredExperienceMapper.toDTO(experience));
  }

  @GetMapping("/view")
  public ResponseEntity<PagedResponse<DeclaredExperienceViewDTO>> getDeclaredExperienceView(
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize,
      @RequestParam(required = false) Boolean isValorized) {
    PagedResult<DeclaredExperience> pagedExperiences =
        declaredExperienceService.getView(new PageCriteria(page, pageSize), isValorized);

    return ResponseEntity.ok(
        new PagedResponse<>(
            pagedExperiences.content().stream().map(declaredExperienceMapper::toDTO).toList(),
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
            request.endDate(),
            Boolean.TRUE.equals(request.valorized()));

    return ResponseEntity.ok(declaredExperienceMapper.toDTO(experience));
  }

  @DeleteMapping("/")
  public ResponseEntity<String> deleteDeclaredExperiences(@RequestBody List<UUID> experienceIds) {
    declaredExperienceService.delete(experienceIds);
    return ResponseEntity.ok("Declared experiences successfully deleted");
  }

  @GetMapping("/search-for-association")
  public ResponseEntity<PagedResponse<AssociationSearchResultDeclaredExperienceDTO>>
      searchDeclaredExperiencesForAssociation(
          Principal principal,
          @RequestParam(required = false) UUID excludeAssociatedWithElementId,
          @Parameter(schema = @Schema(ref = "#/components/schemas/EAssociationContextType"))
              @RequestParam(required = false)
              EAssociationContextType contextType,
          @RequestParam(required = false) String keyword,
          @RequestParam(required = false) Integer page,
          @RequestParam(required = false) Integer pageSize) {
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to search declared experiences for association (contextType={},"
            + " excludeAssociatedWithElementId={}) by student [{}] (keyword={}, page={},"
            + " pageSize={})",
        contextType,
        excludeAssociatedWithElementId,
        principal.getName(),
        keyword,
        pageCriteria.page(),
        pageCriteria.pageSize());

    PagedResult<AssociationSearchResultData> pagedResult =
        declaredExperienceService.searchDeclaredExperiencesForAssociation(
            excludeAssociatedWithElementId, contextType, keyword, pageCriteria);

    return ResponseEntity.ok(
        new PagedResponse<>(
            pagedResult.content().stream()
                .map(associationSearchResultDTOMapper::toDeclaredExperienceDTO)
                .toList(),
            PageInfoDTO.fromDomain(pagedResult.pageInfo())));
  }

  @GetMapping("/{experienceId}/associations")
  public ResponseEntity<DeclaredExperienceAssociationsDTO> getDeclaredExperienceAssociations(
      Principal principal, @PathVariable UUID experienceId) {
    log.debug(
        "Received request to get declared experience[{}] associations by student [{}]",
        experienceId,
        principal.getName());

    var associations = declaredExperienceService.getAssociations(experienceId);

    return ResponseEntity.ok(declaredExperienceMapper.toAssociationsDTO(associations));
  }

  @PostMapping("/{experienceId}/associate/declared-skills")
  public ResponseEntity<DeclaredExperienceAssociationsDTO>
      associateDeclaredExperienceWithDeclaredSkills(
          Principal principal,
          @Valid @PathVariable UUID experienceId,
          @Valid @RequestBody AssociationsCreationRequest body) {
    log.debug(
        "Received request to associate declared experience [{}] with declared skills [{}] by"
            + " student [{}]",
        experienceId,
        body.idsToAssociate(),
        principal.getName());
    var associations =
        declaredExperienceService.associateDeclaredExperienceWithDeclaredSkills(
            experienceId, body.idsToAssociate());
    return ResponseEntity.ok(declaredExperienceMapper.toAssociationsDTO(associations));
  }

  @PostMapping("/{experienceId}/associate/traces")
  public ResponseEntity<DeclaredExperienceAssociationsDTO> associateDeclaredExperienceWithTraces(
      Principal principal,
      @Valid @PathVariable UUID experienceId,
      @Valid @RequestBody AssociationsCreationRequest body) {
    log.debug(
        "Received request to associate declared experience [{}] with traces [{}] by student [{}]",
        experienceId,
        body.idsToAssociate(),
        principal.getName());
    var associations =
        declaredExperienceService.associateDeclaredExperienceWithTraces(
            experienceId, body.idsToAssociate());
    return ResponseEntity.ok(declaredExperienceMapper.toAssociationsDTO(associations));
  }
}
