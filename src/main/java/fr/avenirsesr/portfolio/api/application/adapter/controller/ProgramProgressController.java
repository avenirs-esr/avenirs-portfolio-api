package fr.avenirsesr.portfolio.api.application.adapter.controller;

import fr.avenirsesr.portfolio.api.application.adapter.dto.ProgramProgressOverviewDTO;
import fr.avenirsesr.portfolio.api.application.adapter.dto.ProgramProgressViewDTO;
import fr.avenirsesr.portfolio.api.application.adapter.mapper.ProgramProgressOverviewMapper;
import fr.avenirsesr.portfolio.api.application.adapter.mapper.ProgramProgressViewMapper;
import fr.avenirsesr.portfolio.api.application.adapter.util.UserUtil;
import fr.avenirsesr.portfolio.api.domain.model.Student;
import fr.avenirsesr.portfolio.api.domain.model.enums.ELanguage;
import fr.avenirsesr.portfolio.api.domain.port.input.ProgramProgressService;
import java.security.Principal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/me/program-progress")
public class ProgramProgressController {
  private final ProgramProgressService programProgressService;
  private final UserUtil userUtil;

  @GetMapping("/overview")
  public List<ProgramProgressOverviewDTO> getSkillsOverview(
      Principal principal,
      @RequestHeader(value = "Accept-Language", defaultValue = "fr_FR") String lang) {
    ELanguage language = ELanguage.fromCode(lang);
    Student student = userUtil.getStudent(principal);
    return programProgressService.getSkillsOverview(student, language).entrySet().stream()
        .map(
            entry ->
                ProgramProgressOverviewMapper.fromDomainToDto(entry.getKey(), entry.getValue()))
        .toList();
  }

  @GetMapping("/view")
  public List<ProgramProgressViewDTO> getSkillsView(
      Principal principal,
      @RequestHeader(value = "Accept-Language", defaultValue = "fr_FR") String lang) {
    ELanguage language = ELanguage.fromCode(lang);
    Student student = userUtil.getStudent(principal);
    return programProgressService.getSkillsView(student, language).entrySet().stream()
        .map(entry -> ProgramProgressViewMapper.fromDomainToDto(entry.getKey(), entry.getValue()))
        .toList();
  }
}
