package fr.avenirsesr.portfolio.student.association.application.adapter.controller;

import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.student.association.application.adapter.dto.AssociationSearchResultDTO;
import fr.avenirsesr.portfolio.student.association.application.adapter.dto.AssociationsDTO;
import fr.avenirsesr.portfolio.student.association.application.adapter.mapper.AssociationSearchResultDTOMapper;
import fr.avenirsesr.portfolio.student.association.application.adapter.mapper.AssociationsDTOMapper;
import fr.avenirsesr.portfolio.student.association.application.adapter.request.AssociationsCreationRequest;
import fr.avenirsesr.portfolio.student.association.application.adapter.request.AssociationsDeleteRequest;
import fr.avenirsesr.portfolio.student.association.domain.model.EAssociationContextType;
import fr.avenirsesr.portfolio.student.association.domain.port.input.AssociationService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/me/associations")
public class AssociationController {
  private final AssociationService associationService;
  private final AssociationsDTOMapper associationsDTOMapper;
  private final AssociationSearchResultDTOMapper associationSearchResultDTOMapper;

  @PreAuthorize("hasAuthority('association:manage')")
  @GetMapping("/{contextType}/{elementId}")
  public ResponseEntity<AssociationsDTO> getAssociations(
      Principal principal,
      @Parameter(schema = @Schema(ref = "#/components/schemas/EAssociationContextType"))
          @PathVariable
          EAssociationContextType contextType,
      @Valid @PathVariable UUID elementId,
      @RequestParam(required = false, defaultValue = "false") boolean onlyNotCompleted) {
    log.debug(
        "Received request to get the associations of {} [{}] by student [{}]"
            + " (onlyNotCompleted={})",
        contextType,
        elementId,
        principal.getName(),
        onlyNotCompleted);

    return ResponseEntity.ok(
        associationsDTOMapper.toDTO(
            associationService.getAllAssociatedElementsOf(
                elementId, contextType, onlyNotCompleted)));
  }

  @PreAuthorize("hasAuthority('association:manage')")
  @PostMapping("/{contextType}/{elementId}/{associatedContextType}")
  public ResponseEntity<AssociationsDTO> associate(
      Principal principal,
      @Parameter(schema = @Schema(ref = "#/components/schemas/EAssociationContextType"))
          @PathVariable
          EAssociationContextType contextType,
      @Valid @PathVariable UUID elementId,
      @Parameter(schema = @Schema(ref = "#/components/schemas/EAssociationContextType"))
          @PathVariable
          EAssociationContextType associatedContextType,
      @Valid @RequestBody AssociationsCreationRequest body) {
    log.debug(
        "Received request to associate {} [{}] with {} [{}] by student [{}]",
        contextType,
        elementId,
        associatedContextType,
        body.idsToAssociate(),
        principal.getName());

    return ResponseEntity.ok(
        associationsDTOMapper.toDTO(
            associationService.associate(
                elementId, contextType, associatedContextType, body.idsToAssociate())));
  }

  @PreAuthorize("hasAuthority('association:manage')")
  @DeleteMapping("/{contextType}/{elementId}")
  public ResponseEntity<Void> unassociate(
      Principal principal,
      @Parameter(schema = @Schema(ref = "#/components/schemas/EAssociationContextType"))
          @PathVariable
          EAssociationContextType contextType,
      @Valid @PathVariable UUID elementId,
      @Valid @RequestBody AssociationsDeleteRequest body) {
    log.debug(
        "Received request to unassociate the associations [{}] of {} [{}] by student [{}]",
        body.idsToDelete(),
        contextType,
        elementId,
        principal.getName());

    associationService.unassociate(elementId, contextType, body.idsToDelete());

    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasAuthority('association:manage')")
  @GetMapping("/{contextType}/{elementId}/{associatedContextType}/search")
  public ResponseEntity<PagedResponse<AssociationSearchResultDTO>> searchForAssociation(
      Principal principal,
      @Parameter(schema = @Schema(ref = "#/components/schemas/EAssociationContextType"))
          @PathVariable
          EAssociationContextType contextType,
      @Valid @PathVariable UUID elementId,
      @Parameter(schema = @Schema(ref = "#/components/schemas/EAssociationContextType"))
          @PathVariable
          EAssociationContextType associatedContextType,
      @RequestParam(required = false) String keyword,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize) {
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to search {} for association with {} [{}] by student [{}]"
            + " (keyword={}, page={}, pageSize={})",
        associatedContextType,
        contextType,
        elementId,
        principal.getName(),
        keyword,
        pageCriteria.page(),
        pageCriteria.pageSize());

    var pagedResult =
        associationService.searchForAssociation(
            elementId, contextType, associatedContextType, keyword, pageCriteria);

    return ResponseEntity.ok(
        new PagedResponse<>(
            pagedResult.content().stream().map(associationSearchResultDTOMapper::toDTO).toList(),
            PageInfoDTO.fromDomain(pagedResult.pageInfo())));
  }
}
