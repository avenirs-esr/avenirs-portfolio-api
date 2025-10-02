package fr.avenirsesr.portfolio.additionalskill.application.adapter.controller;

import fr.avenirsesr.portfolio.additionalskill.application.adapter.dto.AdditionalSkillDTO;
import fr.avenirsesr.portfolio.additionalskill.application.adapter.dto.AdditionalSkillProgressDTO;
import fr.avenirsesr.portfolio.additionalskill.application.adapter.mapper.AdditionalSkillMapper;
import fr.avenirsesr.portfolio.additionalskill.application.adapter.mapper.AdditionalSkillProgressMapper;
import fr.avenirsesr.portfolio.additionalskill.application.adapter.request.AddAdditionalSkillDTO;
import fr.avenirsesr.portfolio.additionalskill.domain.model.AdditionalSkillProgress;
import fr.avenirsesr.portfolio.additionalskill.domain.port.input.AdditionalSkillService;
import fr.avenirsesr.portfolio.additionalskill.domain.port.output.OpenSearchIndex;
import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.application.adapter.utils.UserUtil;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.net.URI;
import java.security.Principal;
import java.util.UUID;
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
  private final AdditionalSkillService additionalSkillService;
  private final UserUtil userUtil;

  @GetMapping()
  public ResponseEntity<PagedResponse<AdditionalSkillProgressDTO>> getAdditionalSkillsProgresses(
      Principal principal,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize) {
    Student student = userUtil.getStudent(principal);
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to trace overview of user [{}] (page= {}, size= {})",
        student,
        pageCriteria.page(),
        pageCriteria.pageSize());
    var result = additionalSkillService.getAdditionalSkillsProgresses(student, pageCriteria);
    return ResponseEntity.ok(
        new PagedResponse<>(
            result.content().stream()
                .map(AdditionalSkillProgressMapper::toAdditionalSkillProgressDTO)
                .toList(),
            PageInfoDTO.fromDomain(result.pageInfo())));
  }

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

  @PostMapping()
  public ResponseEntity<AdditionalSkillProgress> createAdditionalSkillProgress(
      Principal principal, @RequestBody AddAdditionalSkillDTO additionalSkill) {
    Student student = userUtil.getStudent(principal);
    log.debug("Received request to create additional skill for student [{}]", student);
    var additionalSkillProgress =
        additionalSkillService.createAdditionalSkillProgress(
            student,
            UUID.fromString(additionalSkill.getId()),
            additionalSkill.getType(),
            additionalSkill.getLevel());
    return ResponseEntity.created(URI.create("/me/additional-skills/" + additionalSkill.getId()))
        .body(additionalSkillProgress);
  }
}
