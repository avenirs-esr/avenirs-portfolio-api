package fr.avenirsesr.portfolio.selfknowledge.application.adapter.controller;

import fr.avenirsesr.portfolio.selfknowledge.application.adapter.dto.SelfKnowledgeCategoryDTO;
import fr.avenirsesr.portfolio.selfknowledge.application.adapter.mapper.SelfKnowledgeCategoryMapper;
import fr.avenirsesr.portfolio.selfknowledge.domain.port.input.SelfKnowledgeService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/me/self-knowledge")
public class SelfKnowledgeController {
  private final SelfKnowledgeService selfKnowledgeService;

  @GetMapping("/categories")
  public ResponseEntity<List<SelfKnowledgeCategoryDTO>> getSelfKnowledgeCategories() {
    return ResponseEntity.ok(
        selfKnowledgeService.getSelfKnowledgeCategories().stream()
            .map(SelfKnowledgeCategoryMapper::toSelfKnowledgeCategoryDTO)
            .toList());
  }
}
