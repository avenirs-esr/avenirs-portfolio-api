package fr.avenirsesr.portfolio.programprogress.application.adapter.controller;

import fr.avenirsesr.portfolio.programprogress.application.adapter.dto.ProgramProgressDTO;
import fr.avenirsesr.portfolio.programprogress.application.adapter.dto.ProgramProgressOverviewDTO;
import fr.avenirsesr.portfolio.programprogress.application.adapter.dto.ProgramProgressViewDTO;
import fr.avenirsesr.portfolio.programprogress.application.adapter.mapper.ProgramProgressMapper;
import fr.avenirsesr.portfolio.programprogress.application.adapter.mapper.ProgramProgressOverviewMapper;
import fr.avenirsesr.portfolio.programprogress.application.adapter.mapper.ProgramProgressViewMapper;
import fr.avenirsesr.portfolio.programprogress.domain.port.input.ProgramProgressService;
import fr.avenirsesr.portfolio.shared.application.adapter.utils.UserUtil;
import fr.avenirsesr.portfolio.shared.domain.model.SortCriteria;
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
@RequestMapping("/me/program-progress")
public class ProgramProgressController {
  private final ProgramProgressService programProgressService;
  private final UserUtil userUtil;

  @GetMapping("/overview")
  public List<ProgramProgressOverviewDTO> getSkillsOverview(Principal principal) {
    Student student = userUtil.getStudent(principal);
    return programProgressService.getSkillsOverview(student).entrySet().stream()
        .map(
            entry ->
                ProgramProgressOverviewMapper.fromDomainToDto(entry.getKey(), entry.getValue()))
        .toList();
  }

  @GetMapping("/view")
  public List<ProgramProgressViewDTO> getSkillsView(
      Principal principal, @RequestParam(name = "sort", required = false) String sortRaw) {
    SortCriteria sortCriteria = SortCriteria.fromString(sortRaw);
    Student student = userUtil.getStudent(principal);

    return programProgressService.getSkillsView(student, sortCriteria).entrySet().stream()
        .map(entry -> ProgramProgressViewMapper.fromDomainToDto(entry.getKey(), entry.getValue()))
        .toList();
  }

  @GetMapping()
  public List<ProgramProgressDTO> getAllProgramProgress(Principal principal) {
    Student student = userUtil.getStudent(principal);
    return programProgressService.getAllProgramProgress(student).stream()
        .map(ProgramProgressMapper::fromDomainToDto)
        .toList();
  }
}
