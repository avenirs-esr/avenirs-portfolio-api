package fr.avenirsesr.portfolio.selfknowledge.application.adapter.controller;

import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.selfknowledge.application.adapter.dto.SelfKnowledgeCategoryDTO;
import fr.avenirsesr.portfolio.selfknowledge.application.adapter.dto.SelfKnowledgeElementDetailsDTO;
import fr.avenirsesr.portfolio.selfknowledge.application.adapter.dto.SelfKnowledgeElementRequest;
import fr.avenirsesr.portfolio.selfknowledge.application.adapter.dto.SelfKnowledgeElementViewDTO;
import fr.avenirsesr.portfolio.selfknowledge.application.adapter.mapper.SelfKnowledgeCategoryMapper;
import fr.avenirsesr.portfolio.selfknowledge.application.adapter.mapper.SelfKnowledgeElementDetailsMapper;
import fr.avenirsesr.portfolio.selfknowledge.application.adapter.mapper.SelfKnowledgeElementViewMapper;
import fr.avenirsesr.portfolio.selfknowledge.domain.data.SelfKnowledgeElementDetails;
import fr.avenirsesr.portfolio.selfknowledge.domain.model.SelfKnowledgeElement;
import fr.avenirsesr.portfolio.selfknowledge.domain.port.input.SelfKnowledgeService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

  @GetMapping("/{selfKnowledgeCategoryId}/elements")
  public ResponseEntity<PagedResponse<SelfKnowledgeElementViewDTO>> getSelfKnowledgeElements(
      @PathVariable("selfKnowledgeCategoryId") UUID selfKnowledgeCategoryId,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize,
      @RequestParam(required = false) Boolean isValorized) {
    log.debug(
        "Received request to get elements of self knowledge category [{}], with"
            + " pagination (page={}, pageSize={}, isValorized={})",
        selfKnowledgeCategoryId,
        page,
        pageSize,
        isValorized);
    PagedResult<SelfKnowledgeElement> selfKnowledgeElementPagedResult =
        selfKnowledgeService.getSelfKnowledgeElements(
            selfKnowledgeCategoryId, new PageCriteria(page, pageSize), isValorized);

    return ResponseEntity.ok(
        new PagedResponse<>(
            selfKnowledgeElementPagedResult.content().stream()
                .map(selfKnowledgeElementViewMapper::toDTO)
                .toList(),
            PageInfoDTO.fromDomain(selfKnowledgeElementPagedResult.pageInfo())));
  }

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

  @PostMapping("/{selfKnowledgeCategoryId}/elements")
  public ResponseEntity<SelfKnowledgeElementViewDTO> createSelfKnowledgeElement(
      @PathVariable("selfKnowledgeCategoryId") UUID selfKnowledgeCategoryId,
      @Valid @RequestBody SelfKnowledgeElementRequest selfKnowledgeElementRequest) {
    log.debug(
        "Received request to create self knowledge element [{}] to category [{}]",
        selfKnowledgeElementRequest,
        selfKnowledgeCategoryId);

    SelfKnowledgeElement selfKnowledgeElement =
        selfKnowledgeService.createSelfKnowledgeElement(
            selfKnowledgeCategoryId,
            selfKnowledgeElementRequest.title(),
            selfKnowledgeElementRequest.description(),
            selfKnowledgeElementRequest.rating());
    return ResponseEntity.ok(selfKnowledgeElementViewMapper.toDTO(selfKnowledgeElement));
  }

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

  @DeleteMapping("/elements")
  public ResponseEntity<String> deleteSelfKnowledgeElements(
      @RequestBody List<UUID> selfKnowledgeElementIds) {
    log.debug("Received request to delete self knowledge elements [{}]", selfKnowledgeElementIds);
    selfKnowledgeService.deleteSelfKnowledgeElements(selfKnowledgeElementIds);
    return ResponseEntity.ok("Self knowledge elements successfully deleted");
  }

  @GetMapping("/categories")
  public ResponseEntity<List<SelfKnowledgeCategoryDTO>> getSelfKnowledgeCategories() {
    return ResponseEntity.ok(
        selfKnowledgeService.getSelfKnowledgeCategories().stream()
            .map(selfKnowledgeCategoryMapper::toSelfKnowledgeCategoryDTO)
            .toList());
  }

  @PostMapping("/categories")
  public ResponseEntity<String> addSelfKnowledgeCategories(@RequestBody List<String> categories) {
    selfKnowledgeService.addSelfKnowledgeCategories(categories);
    return ResponseEntity.ok("Categories successfully associated with user");
  }

  @GetMapping("/categories/available")
  public ResponseEntity<List<SelfKnowledgeCategoryDTO>> getSelfKnowledgeCategoriesAvailable() {
    return ResponseEntity.ok(
        selfKnowledgeService.getSelfKnowledgeCategoriesAvailable().stream()
            .map(selfKnowledgeCategoryMapper::toSelfKnowledgeCategoryDTO)
            .toList());
  }

  @DeleteMapping("/categories/{categoryId}")
  public ResponseEntity<String> removeSelfKnowledgeCategory(@PathVariable UUID categoryId) {
    selfKnowledgeService.removeSelfKnowledgeCategory(categoryId);
    return ResponseEntity.ok("Categories successfully deleted");
  }
}
