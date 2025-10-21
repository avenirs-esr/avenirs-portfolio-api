package fr.avenirsesr.portfolio.student.progress.application.adapter.controller;

import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.program.domain.model.Skill;
import fr.avenirsesr.portfolio.program.domain.model.SkillLevel;
import fr.avenirsesr.portfolio.shared.application.adapter.utils.UserUtil;
import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.SkillDTO;
import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.SkillDetailedDTO;
import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.SkillListItemDTO;
import fr.avenirsesr.portfolio.student.progress.application.adapter.mapper.SkillDetailedMapper;
import fr.avenirsesr.portfolio.student.progress.application.adapter.mapper.SkillMapper;
import fr.avenirsesr.portfolio.student.progress.domain.data.SkillProgressData;
import fr.avenirsesr.portfolio.student.progress.domain.exception.SkillLevelNotFoundException;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillLevelProgress;
import fr.avenirsesr.portfolio.student.progress.domain.port.input.StudentProgressService;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/me/skill-level-progress")
public class SkillLevelProgressController {
  private final StudentProgressService studentProgressService;
  private final UserUtil userUtil;

  @GetMapping()
  public ResponseEntity<PagedResponse<SkillDTO>> getSkillLevelProgresses(
      Principal principal,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize,
      @RequestParam(name = "sort", required = false) String sortRaw) {
    Student student = userUtil.getStudent(principal);

    PagedResult<SkillProgressData> pagedResult =
        studentProgressService.getAllTimeSkillsView(
            student, SortCriteria.fromString(sortRaw), new PageCriteria(page, pageSize));

    var response =
        new PagedResponse<>(
            pagedResult.content().stream()
                .map(
                    skillProgress ->
                        SkillMapper.fromDomainToDto(
                            skillProgress.currentSkillLevelProgress(),
                            skillProgress.studentProgress()))
                .toList(),
            PageInfoDTO.fromDomain(pagedResult.pageInfo()));
    return ResponseEntity.ok(response);
  }

  @GetMapping("/details/{skillId}")
  public ResponseEntity<SkillDetailedDTO> getDetailedSkill(
      Principal principal, @PathVariable UUID skillId) {
    log.debug(
        "Received request to detailed skill [{}] of user [{}]",
        skillId.toString(),
        principal.getName());
    Student student = userUtil.getStudent(principal);
    List<SkillLevelProgress> skillLevelProgresses =
        studentProgressService.getSkillLevelsBySkillId(student, skillId);
    List<SkillLevel> skillLevels =
        skillLevelProgresses.stream().map(SkillLevelProgress::getSkillLevel).toList();
    Skill skill =
        skillLevels.stream()
            .findFirst()
            .orElseThrow(() -> new SkillLevelNotFoundException(skillId.toString()))
            .getSkill();

    var response = SkillDetailedMapper.fromDomainToDto(skill, skillLevels);

    return ResponseEntity.ok(response);
  }

  @GetMapping("/all-skill")
  public ResponseEntity<List<SkillListItemDTO>> getAllSkills(Principal principal) {
    Student student = userUtil.getStudent(principal);
    log.debug("Received request to all skills of [{}]", student);

    var response =
        studentProgressService.getAllSkillList(student).stream()
            .map(skill -> new SkillListItemDTO(skill.getId(), skill.getName()))
            .toList();

    return ResponseEntity.ok(response);
  }
}
