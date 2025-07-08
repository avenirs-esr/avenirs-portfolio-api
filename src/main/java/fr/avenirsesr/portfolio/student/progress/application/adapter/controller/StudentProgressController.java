package fr.avenirsesr.portfolio.student.progress.application.adapter.controller;

import fr.avenirsesr.portfolio.shared.application.adapter.utils.UserUtil;
import fr.avenirsesr.portfolio.shared.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.StudentProgressOverviewDTO;
import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.StudentProgressViewDTO;
import fr.avenirsesr.portfolio.student.progress.application.adapter.mapper.StudentProgressOverviewMapper;
import fr.avenirsesr.portfolio.student.progress.application.adapter.mapper.StudentProgressViewMapper;
import fr.avenirsesr.portfolio.student.progress.domain.port.input.StudentProgressService;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.security.Principal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/me/student-progress")
public class StudentProgressController {
  private final StudentProgressService studentProgressService;
  private final UserUtil userUtil;

  @GetMapping("/overview")
  public List<StudentProgressOverviewDTO> getSkillsOverview(Principal principal) {
    Student student = userUtil.getStudent(principal);
    return studentProgressService.getSkillsOverview(student).entrySet().stream()
        .map(
            entry ->
                StudentProgressOverviewMapper.fromDomainToDto(entry.getKey(), entry.getValue()))
        .toList();
  }

  @GetMapping("/view")
  public List<StudentProgressViewDTO> getSkillsView(
      Principal principal, @RequestParam(name = "sort", required = false) String sortRaw) {
    SortCriteria sortCriteria = SortCriteria.fromString(sortRaw);
    Student student = userUtil.getStudent(principal);

    return studentProgressService.getSkillsView(student, sortCriteria).entrySet().stream()
        .map(entry -> StudentProgressViewMapper.fromDomainToDto(entry.getKey(), entry.getValue()))
        .toList();
  }
}
