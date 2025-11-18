package fr.avenirsesr.portfolio.selfknowledge.application.adapter.controller;

import fr.avenirsesr.portfolio.selfknowledge.application.adapter.dto.SelfKnowledgeCategoryDTO;
import fr.avenirsesr.portfolio.selfknowledge.application.adapter.mapper.SelfKnowledgeCategoryMapper;
import fr.avenirsesr.portfolio.selfknowledge.domain.port.input.SelfKnowledgeService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
