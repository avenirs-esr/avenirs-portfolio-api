package fr.avenirsesr.portfolio.additionalskill.application.adapter.controller;

import fr.avenirsesr.portfolio.additionalskill.application.adapter.dto.AdditionalSkillDTO;
import fr.avenirsesr.portfolio.additionalskill.application.adapter.mapper.AdditionalSkillMapper;
import fr.avenirsesr.portfolio.additionalskill.application.adapter.request.AddAdditionalSkillDTO;
import fr.avenirsesr.portfolio.additionalskill.domain.port.input.AdditionalSkillService;
import fr.avenirsesr.portfolio.shared.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.shared.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.shared.application.adapter.utils.UserUtil;
import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import fr.avenirsesr.portfolio.user.domain.model.User;
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
  private final AdditionalSkillService additionalSkillService;
  private final UserUtil userUtil;

  @GetMapping()
  public ResponseEntity<PagedResponse<AdditionalSkillDTO>> getAdditionalSkills(
      Principal principal,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize) {
    User user = userUtil.getUser(principal);
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to trace overview of user [{}] (page= {}, size= {})",
        user,
        pageCriteria.page(),
        pageCriteria.pageSize());
    var result = additionalSkillService.getAdditionalSkills(pageCriteria);
    return ResponseEntity.ok(
        new PagedResponse<>(
            result.content().stream().map(AdditionalSkillMapper::toAdditionalSkillDTO).toList(),
            PageInfoDTO.fromDomain(result.pageInfo())));
  }

  @GetMapping(params = "keyword")
  public ResponseEntity<PagedResponse<AdditionalSkillDTO>> searchAdditionalSkills(
      @RequestParam String keyword,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize) {
    var result =
        additionalSkillService.searchAdditionalSkills(keyword, new PageCriteria(page, pageSize));
    return ResponseEntity.ok(
        new PagedResponse<>(
            result.content().stream().map(AdditionalSkillMapper::toAdditionalSkillDTO).toList(),
            PageInfoDTO.fromDomain(result.pageInfo())));
  }

  @PostMapping()
  public ResponseEntity<Void> createAdditionalSkill(
      Principal principal, @RequestBody AddAdditionalSkillDTO additionalSkill) {
    Student student = userUtil.getStudent(principal);
    log.debug("Received request to create additional skill for student [{}]", student);
    additionalSkillService.addAdditionalSkills(
        student,
        UUID.fromString(additionalSkill.getId()),
        additionalSkill.getType(),
        additionalSkill.getLevel());
    return ResponseEntity.created(URI.create("/me/additional-skills/" + additionalSkill.getId()))
        .build();
  }
}
