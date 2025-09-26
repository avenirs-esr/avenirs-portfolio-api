package fr.avenirsesr.portfolio.student.progress.application.adapter.controller;

import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.shared.application.adapter.utils.UserUtil;
import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.SkillDTO;
import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.SkillDetailedDTO;
import fr.avenirsesr.portfolio.student.progress.application.adapter.mapper.SkillMapper;
import fr.avenirsesr.portfolio.student.progress.domain.dto.SkillProgressDTO;
import fr.avenirsesr.portfolio.student.progress.domain.port.input.StudentProgressService;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.security.Principal;
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

    PagedResult<SkillProgressDTO> pagedResult =
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
        "Received request to detailed skilled [{}] of user [{}]",
        skillId.toString(),
        principal.getName());
    Student student = userUtil.getStudent(principal);

    var response = studentProgressService.getSkillDetailedById(student, skillId);

    return ResponseEntity.ok(response);
  }
}
