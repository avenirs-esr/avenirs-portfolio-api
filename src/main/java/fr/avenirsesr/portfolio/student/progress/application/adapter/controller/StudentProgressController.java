package fr.avenirsesr.portfolio.student.progress.application.adapter.controller;

import fr.avenirsesr.portfolio.common.data.domain.model.SortCriteria;
import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.StudentProgressDTO;
import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.StudentProgressOverviewDTO;
import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.StudentProgressViewDTO;
import fr.avenirsesr.portfolio.student.progress.application.adapter.mapper.StudentProgressMapper;
import fr.avenirsesr.portfolio.student.progress.application.adapter.mapper.StudentProgressOverviewMapper;
import fr.avenirsesr.portfolio.student.progress.application.adapter.mapper.StudentProgressViewMapper;
import fr.avenirsesr.portfolio.student.progress.domain.port.input.StudentProgressService;
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

  @GetMapping
  public List<StudentProgressDTO> getAllStudentProgress() {
    return studentProgressService.getAllCurrentStudentProgress().stream()
        .map(StudentProgressMapper::toDto)
        .toList();
  }

  @GetMapping("/overview")
  public List<StudentProgressOverviewDTO> getStudentProgressOverview() {
    return studentProgressService.getStudentProgressOverview().entrySet().stream()
        .map(
            entry ->
                StudentProgressOverviewMapper.fromDomainToDto(entry.getKey(), entry.getValue()))
        .toList();
  }

  @GetMapping("/view")
  public List<StudentProgressViewDTO> getStudentProgressView(
      @RequestParam(name = "sort", required = false) String sortRaw) {
    return studentProgressService
        .getStudentProgressView(SortCriteria.fromString(sortRaw))
        .entrySet()
        .stream()
        .map(entry -> StudentProgressViewMapper.fromDomainToDto(entry.getKey(), entry.getValue()))
        .toList();
  }
}
