package fr.avenirsesr.portfolio.additionalskill.application.adapter.controller;

import fr.avenirsesr.portfolio.additionalskill.application.adapter.dto.AdditionalSkillDTO;
import fr.avenirsesr.portfolio.additionalskill.application.adapter.mapper.AdditionalSkillMapper;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.OpenSearchIndex;
import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
<<<<<<< HEAD
=======
import fr.avenirsesr.portfolio.shared.application.adapter.utils.UserUtil;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import jakarta.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.util.UUID;
>>>>>>> 224d802f (feat(AdditionalSkill): check length of description field)
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
<<<<<<< HEAD
=======

  @PostMapping()
  public ResponseEntity<AdditionalSkillProgress> createAdditionalSkillProgress(
      Principal principal, @Valid @RequestBody AddAdditionalSkillDTO additionalSkill) {
    Student student = userUtil.getStudent(principal);
    log.debug("Received request to create additional skill for student [{}]", student);
    var additionalSkillProgress =
        additionalSkillService.createAdditionalSkillProgress(
            student,
            UUID.fromString(additionalSkill.getId()),
            additionalSkill.getType(),
            additionalSkill.getLevel(),
            additionalSkill.getDescription());
    return ResponseEntity.created(URI.create("/me/additional-skills/" + additionalSkill.getId()))
        .body(additionalSkillProgress);
  }
>>>>>>> 224d802f (feat(AdditionalSkill): check length of description field)
}
