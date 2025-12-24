package fr.avenirsesr.portfolio.additionalskill.application.adapter.controller;

import fr.avenirsesr.portfolio.additionalskill.application.adapter.dto.AdditionalSkillDTO;
import fr.avenirsesr.portfolio.additionalskill.infrastructure.adapter.client.ExternalSkillClient;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.externalskill.domain.model.enums.EExternalSkillType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/additional-skills")
public class AdditionalSkillController {
  private final ExternalSkillClient externalSkillClient;

  /**
   * @deprecated This endpoint is obsolete and will be removed in a future version
   */
  @Deprecated(since = "2025-11-25", forRemoval = true)
  @GetMapping(path = "/search")
  public ResponseEntity<PagedResponse<AdditionalSkillDTO>> searchAdditionalSkills(
      @RequestParam String keyword,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize) {
    PagedResponse<
            fr.avenirsesr.portfolio.common.externalskill.application.adapter.dto.ExternalSkillDTO>
        result = externalSkillClient.search(keyword, new PageCriteria(page, pageSize));
    return ResponseEntity.ok(
        new PagedResponse<>(
            result.data().stream()
                .map(
                    externalSkill ->
                        new AdditionalSkillDTO(
                            externalSkill.id(),
                            externalSkill.title(),
                            externalSkill.pathSegments(),
                            EExternalSkillType.valueOf(externalSkill.type().name())))
                .toList(),
            result.page()));
  }
}
