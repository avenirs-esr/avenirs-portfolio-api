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
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/me/self-knowledge")
public class SelfKnowledgeController {
  private final SelfKnowledgeService selfKnowledgeService;

  @GetMapping("/{selfKnowledgeCategoryId}/elements")
  public ResponseEntity<PagedResponse<SelfKnowledgeElementViewDTO>> getSelfKnowledgeElements(
      Principal principal,
      @PathVariable("selfKnowledgeCategoryId") UUID selfKnowledgeCategoryId,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize) {
    log.debug(
        "Received request to get elements of self knowledge category [{}] for user [{}], with"
            + " pagination (page={}, pageSize={})",
        selfKnowledgeCategoryId,
        principal.getName(),
        page,
        pageSize);
    PagedResult<SelfKnowledgeElement> selfKnowledgeElementPagedResult =
        selfKnowledgeService.getSelfKnowledgeElements(
            selfKnowledgeCategoryId, new PageCriteria(page, pageSize));

    return ResponseEntity.ok(
        new PagedResponse<>(
            selfKnowledgeElementPagedResult.content().stream()
                .map(SelfKnowledgeElementViewMapper::toDTO)
                .toList(),
            PageInfoDTO.fromDomain(selfKnowledgeElementPagedResult.pageInfo())));
  }

  @GetMapping("/element/{selfKnowledgeElementId}")
  public ResponseEntity<SelfKnowledgeElementDetailsDTO> getSelfKnowledgeElementDetails(
      Principal principal, @PathVariable("selfKnowledgeElementId") UUID selfKnowledgeElementId) {
    log.debug(
        "Received request to get self knowledge element [{}] details for user [{}]",
        selfKnowledgeElementId,
        principal.getName());

    SelfKnowledgeElementDetails selfKnowledgeElement =
        selfKnowledgeService.getSelfKnowledgeElementDetails(selfKnowledgeElementId);

    return ResponseEntity.ok(
        SelfKnowledgeElementDetailsMapper.toDTO(selfKnowledgeElement.selfKnowledgeElement()));
  }

  @PostMapping("/{selfKnowledgeCategoryId}/elements")
  public ResponseEntity<SelfKnowledgeElementViewDTO> createSelfKnowledgeElement(
      Principal principal,
      @PathVariable("selfKnowledgeCategoryId") UUID selfKnowledgeCategoryId,
      @Valid @RequestBody SelfKnowledgeElementRequest selfKnowledgeElementRequest) {
    log.debug(
        "Received request to create self knowledge element [{}] to category [{}] for user [{}]",
        selfKnowledgeElementRequest,
        selfKnowledgeCategoryId,
        principal.getName());

    SelfKnowledgeElement selfKnowledgeElement =
        selfKnowledgeService.createSelfKnowledgeElement(
            selfKnowledgeCategoryId, selfKnowledgeElementRequest);
    return ResponseEntity.ok(SelfKnowledgeElementViewMapper.toDTO(selfKnowledgeElement));
  }

  @DeleteMapping("/element/{selfKnowledgeElementId}")
  public ResponseEntity<String> deleteSelfKnowledgeElement(
      Principal principal, @PathVariable("selfKnowledgeElementId") UUID selfKnowledgeElementId) {
    log.debug(
        "Received request to delete self knowledge element [{}] for user [{}]",
        selfKnowledgeElementId,
        principal.getName());
    selfKnowledgeService.deleteSelfKnowledgeElement(selfKnowledgeElementId);
    return ResponseEntity.ok("Self knowledge element successfully deleted");
  }

  @GetMapping("/categories")
  public ResponseEntity<List<SelfKnowledgeCategoryDTO>> getSelfKnowledgeCategories() {
    return ResponseEntity.ok(
        selfKnowledgeService.getSelfKnowledgeCategories().stream()
            .map(SelfKnowledgeCategoryMapper::toSelfKnowledgeCategoryDTO)
            .toList());
  }

  @PostMapping("/categories")
  public ResponseEntity<String> getSelfKnowledgeCategories(@RequestBody List<String> categories) {
    selfKnowledgeService.addSelfKnowledgeCategories(categories);
    return ResponseEntity.ok("Categories successfully associated with user");
  }

  @GetMapping("/categories/available")
  public ResponseEntity<List<SelfKnowledgeCategoryDTO>> getSelfKnowledgeCategoriesAvailable() {
    return ResponseEntity.ok(
        selfKnowledgeService.getSelfKnowledgeCategoriesAvailable().stream()
            .map(SelfKnowledgeCategoryMapper::toSelfKnowledgeCategoryDTO)
            .toList());
  }
}
