package fr.avenirsesr.portfolio.student.skill.application.adapter.controller;

import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.student.skill.application.adapter.dto.DeclaredSkillProgressDTO;
import fr.avenirsesr.portfolio.student.skill.application.adapter.dto.DeclaredSkillProgressDetailsDTO;
import fr.avenirsesr.portfolio.student.skill.application.adapter.dto.DeclaredSkillProgressRequest;
import fr.avenirsesr.portfolio.student.skill.application.adapter.mapper.DeclaredSkillProgressMapper;
import fr.avenirsesr.portfolio.student.skill.application.adapter.request.AddDeclaredSkillDTO;
import fr.avenirsesr.portfolio.student.skill.domain.data.DeclaredSkillProgressDetails;
import fr.avenirsesr.portfolio.student.skill.domain.port.input.DeclaredSkillProgressService;
import jakarta.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/me/declared/skill-progress")
public class DeclaredSkillProgressController {
  private final DeclaredSkillProgressService declaredSkillProgressService;
  private final DeclaredSkillProgressMapper declaredSkillProgressMapper;

  @PreAuthorize("hasAuthority('declared-skill:list:own')")
  @GetMapping()
  public ResponseEntity<PagedResponse<DeclaredSkillProgressDTO>> getDeclaredSkillsProgresses(
      Principal principal,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize,
      @RequestParam(required = false) Boolean isValorized) {
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to trace overview of user [{}] (page= {}, fileSize= {}, isValorized={})",
        principal.getName(),
        pageCriteria.page(),
        pageCriteria.pageSize(),
        isValorized);
    var result =
        declaredSkillProgressService.getDeclaredSkillsProgresses(pageCriteria, isValorized);
    return ResponseEntity.ok(
        new PagedResponse<>(
            result.content().stream()
                .map(declaredSkillProgressMapper::toDeclaredSkillProgressDTO)
                .toList(),
            PageInfoDTO.fromDomain(result.pageInfo())));
  }

  @PreAuthorize("hasAuthority('declared-skill:create:own')")
  @PostMapping()
  public ResponseEntity<DeclaredSkillProgressDTO> createDeclaredSkillProgress(
      Principal principal, @RequestBody AddDeclaredSkillDTO declaredSkill) {
    log.debug("Received request to create declared skill for student [{}]", principal.getName());
    var declaredSkillProgress =
        declaredSkillProgressService.createDeclaredSkillProgress(
            UUID.fromString(declaredSkill.getId()),
            declaredSkill.getType(),
            declaredSkill.getLevel(),
            declaredSkill.getReflection());
    return ResponseEntity.created(
            URI.create("/me/declared/skill-progress/" + declaredSkill.getId()))
        .body(declaredSkillProgressMapper.toDeclaredSkillProgressDTO(declaredSkillProgress));
  }

  @PreAuthorize("hasAuthority('declared-skill:update:own')")
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
            declaredSkillProgressRequest.reflection(),
            declaredSkillProgressRequest.valorized());
    return ResponseEntity.ok(
        declaredSkillProgressMapper.toDeclaredSkillProgressDTO(declaredSkillProgress));
  }

  @PreAuthorize("hasAuthority('declared-skill:list:own')")
  @GetMapping("/external-ids")
  public ResponseEntity<List<UUID>> getAssociatedExternalSkillIds(Principal principal) {
    log.debug(
        "Received request to get associated external skill ids for student [{}]",
        principal.getName());
    return ResponseEntity.ok(declaredSkillProgressService.getAssociatedExternalSkillIds());
  }

  @PreAuthorize("hasAuthority('declared-skill:list:own')")
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
        declaredSkillProgressMapper.toDeclaredSkillProgressDetailsDTO(
            declaredSkillProgressDetails));
  }

  @PreAuthorize("hasAuthority('declared-skill:delete:own')")
  @DeleteMapping("/{declaredSkillProgressId}")
  public ResponseEntity<String> deleteDeclaredSkillProgress(
      @PathVariable UUID declaredSkillProgressId) {
    declaredSkillProgressService.deleteDeclaredSkillProgresses(List.of(declaredSkillProgressId));
    return ResponseEntity.ok("Declared skill progress successfully deleted");
  }

  @PreAuthorize("hasAuthority('declared-skill:delete:own')")
  @DeleteMapping()
  public ResponseEntity<String> deleteDeclaredSkillProgresses(
      @RequestBody List<UUID> declaredSkillProgressIds) {
    declaredSkillProgressService.deleteDeclaredSkillProgresses(declaredSkillProgressIds);
    return ResponseEntity.ok("Declared skill progresses successfully deleted");
  }
}
