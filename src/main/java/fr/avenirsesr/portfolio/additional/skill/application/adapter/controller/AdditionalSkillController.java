package fr.avenirsesr.portfolio.additional.skill.application.adapter.controller;

import fr.avenirsesr.portfolio.additional.skill.application.adapter.dto.AdditionalSkillDTO;
import fr.avenirsesr.portfolio.additional.skill.application.adapter.mapper.AdditionalSkillMapper;
import fr.avenirsesr.portfolio.additional.skill.domain.port.input.AdditionalSkillService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/me/additional-skill")
public class AdditionalSkillController {
  private final AdditionalSkillService additionalSkillService;

  @GetMapping()
  public ResponseEntity<List<AdditionalSkillDTO>> getAdditionalSkills() {
    return ResponseEntity.ok(
        additionalSkillService.getAdditionalSkills().stream()
            .map(AdditionalSkillMapper::toAdditionalSkillDTO)
            .toList());
  }
}
