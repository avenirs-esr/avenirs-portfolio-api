package fr.avenirsesr.portfolio.student.selfknowledge.application.adapter.controller;

import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.student.selfknowledge.application.adapter.dto.SelfKnowledgeCategoryDTO;
import fr.avenirsesr.portfolio.student.selfknowledge.application.adapter.dto.SelfKnowledgeElementDetailsDTO;
import fr.avenirsesr.portfolio.student.selfknowledge.application.adapter.dto.SelfKnowledgeElementRequest;
import fr.avenirsesr.portfolio.student.selfknowledge.application.adapter.dto.SelfKnowledgeElementViewDTO;
import fr.avenirsesr.portfolio.student.selfknowledge.application.adapter.mapper.SelfKnowledgeCategoryMapper;
import fr.avenirsesr.portfolio.student.selfknowledge.application.adapter.mapper.SelfKnowledgeElementDetailsMapper;
import fr.avenirsesr.portfolio.student.selfknowledge.application.adapter.mapper.SelfKnowledgeElementViewMapper;
import fr.avenirsesr.portfolio.student.selfknowledge.domain.data.SelfKnowledgeElementDetails;
import fr.avenirsesr.portfolio.student.selfknowledge.domain.model.SelfKnowledgeElement;
import fr.avenirsesr.portfolio.student.selfknowledge.domain.model.enums.ESelfKnowledgeCategory;
import fr.avenirsesr.portfolio.student.selfknowledge.domain.port.input.SelfKnowledgeService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/me/self-knowledge")
public class SelfKnowledgeController {
  private final SelfKnowledgeService selfKnowledgeService;
  private final SelfKnowledgeCategoryMapper selfKnowledgeCategoryMapper;
  private final SelfKnowledgeElementDetailsMapper selfKnowledgeElementDetailsMapper;
  private final SelfKnowledgeElementViewMapper selfKnowledgeElementViewMapper;

  @PreAuthorize("hasAuthority('self-knowledge:list:own')")
  @GetMapping("/elements")
  public ResponseEntity<PagedResponse<SelfKnowledgeElementViewDTO>> getSelfKnowledgeElements(
      @Parameter(
              array =
                  @ArraySchema(
                      schema = @Schema(ref = "#/components/schemas/ESelfKnowledgeCategory")))
          @RequestParam(required = false)
          List<ESelfKnowledgeCategory> selfKnowledgeCategories,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize,
      @RequestParam(required = false) Boolean isValorized) {
    log.debug(
        "Received request to get elements of self knowledge categories [{}], with"
            + " pagination (page={}, pageSize={}, isValorized={})",
        selfKnowledgeCategories,
        page,
        pageSize,
        isValorized);
    PagedResult<SelfKnowledgeElement> selfKnowledgeElementPagedResult =
        selfKnowledgeService.getSelfKnowledgeElements(
            selfKnowledgeCategories, new PageCriteria(page, pageSize), isValorized);

    return ResponseEntity.ok(
        new PagedResponse<>(
            selfKnowledgeElementPagedResult.content().stream()
                .map(selfKnowledgeElementViewMapper::toDTO)
                .toList(),
            PageInfoDTO.fromDomain(selfKnowledgeElementPagedResult.pageInfo())));
  }

  @PreAuthorize("hasAuthority('self-knowledge:list:own')")
  @GetMapping("/element/{selfKnowledgeElementId}")
  public ResponseEntity<SelfKnowledgeElementDetailsDTO> getSelfKnowledgeElementDetails(
      @PathVariable("selfKnowledgeElementId") UUID selfKnowledgeElementId) {
    log.debug(
        "Received request to get self knowledge element [{}] details", selfKnowledgeElementId);

    SelfKnowledgeElementDetails selfKnowledgeElement =
        selfKnowledgeService.getSelfKnowledgeElementDetails(selfKnowledgeElementId);

    return ResponseEntity.ok(
        selfKnowledgeElementDetailsMapper.toDTO(selfKnowledgeElement.selfKnowledgeElement()));
  }

  @PreAuthorize("hasAuthority('self-knowledge:create:own')")
  @PostMapping("/{selfKnowledgeCategory}/elements")
  public ResponseEntity<SelfKnowledgeElementViewDTO> createSelfKnowledgeElement(
      @Parameter(
              name = "selfKnowledgeCategory",
              in = ParameterIn.PATH,
              required = true,
              schema = @Schema(ref = "#/components/schemas/ESelfKnowledgeCategory"))
          @PathVariable("selfKnowledgeCategory")
          ESelfKnowledgeCategory selfKnowledgeCategory,
      @Valid @RequestBody SelfKnowledgeElementRequest selfKnowledgeElementRequest) {
    log.debug(
        "Received request to create self knowledge element [{}] to category [{}]",
        selfKnowledgeElementRequest,
        selfKnowledgeCategory);

    SelfKnowledgeElement selfKnowledgeElement =
        selfKnowledgeService.createSelfKnowledgeElement(
            selfKnowledgeCategory,
            selfKnowledgeElementRequest.title(),
            selfKnowledgeElementRequest.description(),
            selfKnowledgeElementRequest.rating());
    return ResponseEntity.ok(selfKnowledgeElementViewMapper.toDTO(selfKnowledgeElement));
  }

  @PreAuthorize("hasAuthority('self-knowledge:update:own')")
  @PutMapping("/element/{selfKnowledgeElementId}")
  public ResponseEntity<SelfKnowledgeElementViewDTO> updateSelfKnowledgeElement(
      @PathVariable("selfKnowledgeElementId") UUID selfKnowledgeElementId,
      @Valid @RequestBody SelfKnowledgeElementRequest selfKnowledgeElementRequest) {
    log.debug(
        "Received request to update self knowledge element [{}] with infos [{}]",
        selfKnowledgeElementId,
        selfKnowledgeElementRequest);

    SelfKnowledgeElement selfKnowledgeElement =
        selfKnowledgeService.updateSelfKnowledgeElement(
            selfKnowledgeElementId,
            selfKnowledgeElementRequest.title(),
            selfKnowledgeElementRequest.description(),
            selfKnowledgeElementRequest.rating(),
            selfKnowledgeElementRequest.valorized());

    return ResponseEntity.ok(selfKnowledgeElementViewMapper.toDTO(selfKnowledgeElement));
  }

  @PreAuthorize("hasAuthority('self-knowledge:delete:own')")
  @DeleteMapping("/elements")
  public ResponseEntity<String> deleteSelfKnowledgeElements(
      @RequestBody List<UUID> selfKnowledgeElementIds) {
    log.debug("Received request to delete self knowledge elements [{}]", selfKnowledgeElementIds);
    selfKnowledgeService.deleteSelfKnowledgeElements(selfKnowledgeElementIds);
    return ResponseEntity.ok("Self knowledge elements successfully deleted");
  }

  @PreAuthorize("hasAuthority('self-knowledge:list:own')")
  @GetMapping("/categories")
  public ResponseEntity<List<SelfKnowledgeCategoryDTO>> getSelfKnowledgeCategories() {
    return ResponseEntity.ok(
        selfKnowledgeService.getSelfKnowledgeCategories().stream()
            .map(selfKnowledgeCategoryMapper::toSelfKnowledgeCategoryDTO)
            .toList());
  }

  @PreAuthorize("hasAuthority('self-knowledge:create:own')")
  @PostMapping("/categories")
  public ResponseEntity<String> addSelfKnowledgeCategories(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
              content =
                  @Content(
                      array =
                          @ArraySchema(
                              schema =
                                  @Schema(ref = "#/components/schemas/ESelfKnowledgeCategory"))))
          @RequestBody
          List<ESelfKnowledgeCategory> categories) {
    selfKnowledgeService.addSelfKnowledgeCategories(categories);
    return ResponseEntity.ok("Categories successfully associated with user");
  }

  @PreAuthorize("hasAuthority('self-knowledge:list:own')")
  @GetMapping("/categories/available")
  public ResponseEntity<List<SelfKnowledgeCategoryDTO>> getSelfKnowledgeCategoriesAvailable() {
    return ResponseEntity.ok(
        selfKnowledgeService.getSelfKnowledgeCategoriesAvailable().stream()
            .map(selfKnowledgeCategoryMapper::toSelfKnowledgeCategoryDTO)
            .toList());
  }

  @PreAuthorize("hasAuthority('self-knowledge:delete:own')")
  @DeleteMapping("/categories/{selfKnowledgeCategory}")
  public ResponseEntity<String> removeSelfKnowledgeCategory(
      @Parameter(
              name = "selfKnowledgeCategory",
              in = ParameterIn.PATH,
              required = true,
              schema = @Schema(ref = "#/components/schemas/ESelfKnowledgeCategory"))
          @PathVariable
          ESelfKnowledgeCategory selfKnowledgeCategory) {
    selfKnowledgeService.removeSelfKnowledgeCategory(selfKnowledgeCategory);
    return ResponseEntity.ok("Categories successfully deleted");
  }
}
