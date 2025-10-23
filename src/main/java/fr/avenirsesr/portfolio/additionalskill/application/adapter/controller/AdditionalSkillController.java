package fr.avenirsesr.portfolio.additionalskill.application.adapter.controller;

import fr.avenirsesr.portfolio.additionalskill.application.adapter.dto.AdditionalSkillDTO;
import fr.avenirsesr.portfolio.additionalskill.application.adapter.mapper.AdditionalSkillMapper;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.OpenSearchIndex;
import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/me/additional-skills")
public class AdditionalSkillController {
  private final OpenSearchIndex openSearchIndex;

  @GetMapping(path = "/search")
  public ResponseEntity<PagedResponse<AdditionalSkillDTO>> searchAdditionalSkills(
      @RequestParam String keyword,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize) {
    var result = openSearchIndex.search(keyword, new PageCriteria(page, pageSize));
    return ResponseEntity.ok(
        new PagedResponse<>(
            result.content().stream().map(AdditionalSkillMapper::toAdditionalSkillDTO).toList(),
            PageInfoDTO.fromDomain(result.pageInfo())));
  }
}
