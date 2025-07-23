package fr.avenirsesr.portfolio.student.progress.application.adapter.controller;

import fr.avenirsesr.portfolio.shared.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.shared.application.adapter.utils.UserUtil;
import fr.avenirsesr.portfolio.shared.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.domain.model.PagedResult;
import fr.avenirsesr.portfolio.shared.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.student.progress.application.adapter.mapper.SkillViewMapper;
import fr.avenirsesr.portfolio.student.progress.application.adapter.response.SkillLifeProjectViewResponse;
import fr.avenirsesr.portfolio.student.progress.domain.model.SkillProgress;
import fr.avenirsesr.portfolio.student.progress.domain.port.input.StudentProgressService;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.security.Principal;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

  @GetMapping("/")
  public ResponseEntity<SkillLifeProjectViewResponse> get(
      Principal principal,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize,
      @RequestParam(name = "sort", required = false) String sortRaw) {
    Student student = userUtil.getStudent(principal);

    PagedResult<SkillProgress> pagedResult =
        studentProgressService.getSkillsLifeProjectView(
            student, SortCriteria.fromString(sortRaw), new PageCriteria(page, pageSize));

    var response =
        new SkillLifeProjectViewResponse(
            pagedResult.content().stream()
                .map(
                    skillProgress ->
                        SkillViewMapper.fromDomainToDto(
                            skillProgress.currentSkillLevelProgress(),
                            skillProgress.studentProgress()))
                .toList(),
            PageInfoDTO.fromDomain(pagedResult.pageInfo()));
    return ResponseEntity.ok(response);
  }
}
