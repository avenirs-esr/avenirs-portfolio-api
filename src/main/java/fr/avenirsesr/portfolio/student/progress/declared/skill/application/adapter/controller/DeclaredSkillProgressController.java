package fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.controller;

import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.dto.DeclaredSkillProgressDTO;
import fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.dto.DeclaredSkillProgressDetailsDTO;
import fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.dto.DeclaredSkillProgressRequest;
import fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.mapper.DeclaredSkillProgressMapper;
import fr.avenirsesr.portfolio.student.progress.declared.skill.application.adapter.request.AddDeclaredSkillDTO;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.data.DeclaredSkillProgressDetails;
import fr.avenirsesr.portfolio.student.progress.declared.skill.domain.port.input.DeclaredSkillProgressService;
import jakarta.validation.Valid;
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
@RequestMapping("/me/declared/skill-progress")
public class DeclaredSkillProgressController {
  private final DeclaredSkillProgressService declaredSkillProgressService;

  @GetMapping()
  public ResponseEntity<PagedResponse<DeclaredSkillProgressDTO>> getDeclaredSkillsProgresses(
      Principal principal,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize) {
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to trace overview of user [{}] (page= {}, fileSize= {})",
        principal.getName(),
        pageCriteria.page(),
        pageCriteria.pageSize());
    var result = declaredSkillProgressService.getDeclaredSkillsProgresses(pageCriteria);
    return ResponseEntity.ok(
        new PagedResponse<>(
            result.content().stream()
                .map(DeclaredSkillProgressMapper::toDeclaredSkillProgressDTO)
                .toList(),
            PageInfoDTO.fromDomain(result.pageInfo())));
  }

  @PostMapping()
  public ResponseEntity<DeclaredSkillProgressDTO> createDeclaredSkillProgress(
      Principal principal, @RequestBody AddDeclaredSkillDTO declaredSkill) {
    log.debug("Received request to create declared skill for student [{}]", principal.getName());
    var declaredSkillProgress =
        declaredSkillProgressService.createDeclaredSkillProgress(
            UUID.fromString(declaredSkill.getId()),
            declaredSkill.getType(),
            declaredSkill.getLevel(),
            declaredSkill.getDescription());
    return ResponseEntity.created(
            URI.create("/me/declared/skill-progress/" + declaredSkill.getId()))
        .body(DeclaredSkillProgressMapper.toDeclaredSkillProgressDTO(declaredSkillProgress));
  }

  @PutMapping("/{declaredSkillProgressId}")
  public ResponseEntity<DeclaredSkillProgressDTO> updateDeclaredSkillProgress(
      Principal principal,
      @PathVariable UUID declaredSkillProgressId,
      @Valid @RequestBody DeclaredSkillProgressRequest declaredSkillProgressRequest) {
    log.debug(
        "Received request to update declared skill progress for student [{}]", principal.getName());
    var declaredSkillProgress =
        declaredSkillProgressService.updateDeclaredSkillProgress(
            declaredSkillProgressId,
            declaredSkillProgressRequest.level(),
            declaredSkillProgressRequest.description());
    return ResponseEntity.ok(
        DeclaredSkillProgressMapper.toDeclaredSkillProgressDTO(declaredSkillProgress));
  }

  @GetMapping("/{declaredSkillProgressId}")
  public ResponseEntity<DeclaredSkillProgressDetailsDTO> getDeclaredSkillProgressDetails(
      Principal principal, @PathVariable UUID declaredSkillProgressId) {
    log.debug(
        "Received request to detailed declared skill progress [{}] for student [{}]",
        declaredSkillProgressId,
        principal.getName());
    DeclaredSkillProgressDetails declaredSkillProgressDetails =
        declaredSkillProgressService.getDeclaredSkillProgressDetails(declaredSkillProgressId);

    return ResponseEntity.ok(
        DeclaredSkillProgressMapper.toDeclaredSkillProgressDetailsDTO(
            declaredSkillProgressDetails));
  }

  @DeleteMapping("/{declaredSkillProgressId}")
  public ResponseEntity<String> deleteDeclaredSkillProgress(
      @PathVariable UUID declaredSkillProgressId) {
    declaredSkillProgressService.deleteDeclaredSkillProgresses(List.of(declaredSkillProgressId));
    return ResponseEntity.ok("Declared skill progress successfully deleted");
  }

  @DeleteMapping()
  public ResponseEntity<String> deleteDeclaredSkillProgresses(
      @RequestBody List<UUID> declaredSkillProgressIds) {
    declaredSkillProgressService.deleteDeclaredSkillProgresses(declaredSkillProgressIds);
    return ResponseEntity.ok("Declared skill progresses successfully deleted");
  }

  @PostMapping("/{declaredSkillProgressId}/unassociate/traces")
  public ResponseEntity<String> unassociateTraces(
      Principal principal,
      @PathVariable UUID declaredSkillProgressId,
      @RequestBody List<UUID> traceIds) {
    log.debug(
        "Received request to unassociate traces [{}] to declared skill progress [{}] for student"
            + " [{}]",
        traceIds,
        declaredSkillProgressId,
        principal.getName());

    declaredSkillProgressService.unassociateTraces(declaredSkillProgressId, traceIds);

    return ResponseEntity.ok("Trace successfully unassociated.");
  }
}
