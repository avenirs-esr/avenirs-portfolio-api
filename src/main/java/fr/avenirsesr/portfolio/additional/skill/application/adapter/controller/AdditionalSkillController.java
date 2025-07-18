package fr.avenirsesr.portfolio.additional.skill.application.adapter.controller;

import fr.avenirsesr.portfolio.additional.skill.application.adapter.mapper.AdditionalSkillMapper;
import fr.avenirsesr.portfolio.additional.skill.application.adapter.response.AdditionalSkillResponse;
import fr.avenirsesr.portfolio.additional.skill.domain.port.input.AdditionalSkillService;
import fr.avenirsesr.portfolio.shared.application.adapter.utils.UserUtil;
import fr.avenirsesr.portfolio.user.domain.model.User;
import java.security.Principal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/me/additional-skills")
public class AdditionalSkillController {
  private final AdditionalSkillService additionalSkillService;
  private final UserUtil userUtil;

  @GetMapping()
  public ResponseEntity<AdditionalSkillResponse> getAdditionalSkills(
      Principal principal,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize) {
    User user = userUtil.getUser(principal);
    log.debug("Received request to trace overview of user [{}]", user);
    var result = additionalSkillService.getAdditionalSkills(page, pageSize);
    return ResponseEntity.ok(
        new AdditionalSkillResponse(
            result.data().stream().map(AdditionalSkillMapper::toAdditionalSkillDTO).toList(),
            result.page()));
  }
}
