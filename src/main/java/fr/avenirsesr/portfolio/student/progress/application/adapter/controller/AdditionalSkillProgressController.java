package fr.avenirsesr.portfolio.student.progress.application.adapter.controller;

import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.shared.application.adapter.utils.UserUtil;
import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.AdditionalSkillProgressDTO;
import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.AdditionalSkillProgressDetailsDTO;
import fr.avenirsesr.portfolio.student.progress.application.adapter.mapper.AdditionalSkillProgressMapper;
import fr.avenirsesr.portfolio.student.progress.application.adapter.request.AddAdditionalSkillDTO;
import fr.avenirsesr.portfolio.student.progress.domain.port.input.StudentProgressService;
import fr.avenirsesr.portfolio.trace.application.adapter.dto.TraceOverviewDTO;
import fr.avenirsesr.portfolio.trace.application.adapter.mapper.TraceOverviewMapper;
import fr.avenirsesr.portfolio.trace.domain.model.Trace;
import fr.avenirsesr.portfolio.trace.domain.port.input.TraceService;
import fr.avenirsesr.portfolio.user.domain.model.Student;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/me/additional-skill-progress")
public class AdditionalSkillProgressController {
  private final StudentProgressService studentProgressService;
  private final TraceService traceService;
  private final UserUtil userUtil;

  @GetMapping()
  public ResponseEntity<PagedResponse<AdditionalSkillProgressDTO>> getAdditionalSkillsProgresses(
      Principal principal,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize) {
    Student student = userUtil.getStudent(principal);
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to trace overview of user [{}] (page= {}, size= {})",
        student,
        pageCriteria.page(),
        pageCriteria.pageSize());
    var result = studentProgressService.getAdditionalSkillsProgresses(student, pageCriteria);
    return ResponseEntity.ok(
        new PagedResponse<>(
            result.content().stream()
                .map(AdditionalSkillProgressMapper::toAdditionalSkillProgressDTO)
                .toList(),
            PageInfoDTO.fromDomain(result.pageInfo())));
  }

  @PostMapping()
  public ResponseEntity<AdditionalSkillProgressDTO> createAdditionalSkillProgress(
      Principal principal, @RequestBody AddAdditionalSkillDTO additionalSkill) {
    Student student = userUtil.getStudent(principal);
    log.debug("Received request to create additional skill for student [{}]", student);
    var additionalSkillProgress =
        studentProgressService.createAdditionalSkillProgress(
            student,
            UUID.fromString(additionalSkill.getId()),
            additionalSkill.getType(),
            additionalSkill.getLevel(),
            additionalSkill.getDescription());
    return ResponseEntity.created(URI.create("/me/additional-skills/" + additionalSkill.getId()))
        .body(AdditionalSkillProgressMapper.toAdditionalSkillProgressDTO(additionalSkillProgress));
  }

  @GetMapping("/{additionalSkillId}")
  public ResponseEntity<AdditionalSkillProgressDetailsDTO> getAdditionalSkillProgressDetails(
      Principal principal, @PathVariable UUID additionalSkillId) {
    Student student = userUtil.getStudent(principal);
    log.debug(
        "Received request to detailed additional skill [{}] for student [{}]",
        additionalSkillId,
        student);
    AdditionalSkillProgress additionalSkillProgress =
        studentProgressService.getAdditionalSkillProgressDetails(student, additionalSkillId);
    List<Trace> traces =
        traceService.getTracesByAdditionalSkillProgressId(additionalSkillProgress.getId());
    List<TraceOverviewDTO> traceOverviewDTOs =
        traces.stream()
            .map(trace -> TraceOverviewMapper.toDTO(trace, traceService.programNameOfTrace(trace)))
            .toList();

    return ResponseEntity.ok(
        AdditionalSkillProgressMapper.toAdditionalSkillProgressDetailsDTO(
            additionalSkillProgress, traceOverviewDTOs));
  }
}
