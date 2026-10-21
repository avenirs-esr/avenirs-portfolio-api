package fr.avenirsesr.portfolio.student.skill.application.adapter.controller;

import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.student.skill.application.adapter.dto.SkillProgressDTO;
import fr.avenirsesr.portfolio.student.skill.application.adapter.dto.SkillProgressDetailsDTO;
import fr.avenirsesr.portfolio.student.skill.application.adapter.dto.SkillProgressRequest;
import fr.avenirsesr.portfolio.student.skill.application.adapter.mapper.SkillProgressMapper;
import fr.avenirsesr.portfolio.student.skill.application.adapter.request.AddSkillProgressDTO;
import fr.avenirsesr.portfolio.student.skill.domain.data.SkillProgressDetails;
import fr.avenirsesr.portfolio.student.skill.domain.port.input.SkillProgressService;
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
@RequestMapping("/me/skill-progress")
public class SkillProgressController {
  private final SkillProgressService skillProgressService;
  private final SkillProgressMapper skillProgressMapper;

  @PreAuthorize("hasAuthority('declared-skill:list:own')")
  @GetMapping()
  public ResponseEntity<PagedResponse<SkillProgressDTO>> getSkillProgresses(
      Principal principal,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize,
      @RequestParam(required = false) Boolean isValorized) {
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to list the skill progresses of user [{}] (page= {}, fileSize= {}, isValorized={})",
        principal.getName(),
        pageCriteria.page(),
        pageCriteria.pageSize(),
        isValorized);
    var result = skillProgressService.getSkillProgresses(pageCriteria, isValorized);
    return ResponseEntity.ok(
        new PagedResponse<>(
            result.content().stream().map(skillProgressMapper::toSkillProgressDTO).toList(),
            PageInfoDTO.fromDomain(result.pageInfo())));
  }

  @PreAuthorize("hasAuthority('declared-skill:create:own')")
  @PostMapping()
  public ResponseEntity<SkillProgressDTO> createSkillProgress(
      Principal principal, @RequestBody AddSkillProgressDTO skillProgressToAdd) {
    log.debug("Received request to create a skill progress for student [{}]", principal.getName());
    var skillProgress =
        skillProgressService.createSkillProgress(
            UUID.fromString(skillProgressToAdd.getId()),
            skillProgressToAdd.getLevel(),
            skillProgressToAdd.getReflection());
    return ResponseEntity.created(URI.create("/me/skill-progress/" + skillProgressToAdd.getId()))
        .body(skillProgressMapper.toSkillProgressDTO(skillProgress));
  }

  @PreAuthorize("hasAuthority('declared-skill:update:own')")
  @PutMapping("/{skillProgressId}")
  public ResponseEntity<SkillProgressDTO> updateSkillProgress(
      Principal principal,
      @PathVariable UUID skillProgressId,
      @Valid @RequestBody SkillProgressRequest skillProgressRequest) {
    log.debug("Received request to update a skill progress for student [{}]", principal.getName());
    var skillProgress =
        skillProgressService.updateSkillProgress(
            skillProgressId,
            skillProgressRequest.level(),
            skillProgressRequest.reflection(),
            skillProgressRequest.valorized());
    return ResponseEntity.ok(skillProgressMapper.toSkillProgressDTO(skillProgress));
  }

  @PreAuthorize("hasAuthority('declared-skill:list:own')")
  @GetMapping("/skill-ids")
  public ResponseEntity<List<UUID>> getSkillIds(Principal principal) {
    log.debug("Received request to get the skill ids for student [{}]", principal.getName());
    return ResponseEntity.ok(skillProgressService.getSkillIds());
  }

  @PreAuthorize("hasAuthority('declared-skill:list:own')")
  @GetMapping("/{skillProgressId}")
  public ResponseEntity<SkillProgressDetailsDTO> getSkillProgressDetails(
      Principal principal, @PathVariable UUID skillProgressId) {
    log.debug(
        "Received request to detail the skill progress [{}] for student [{}]",
        skillProgressId,
        principal.getName());
    SkillProgressDetails skillProgressDetails =
        skillProgressService.getSkillProgressDetails(skillProgressId);

    return ResponseEntity.ok(skillProgressMapper.toSkillProgressDetailsDTO(skillProgressDetails));
  }

  @PreAuthorize("hasAuthority('declared-skill:delete:own')")
  @DeleteMapping("/{skillProgressId}")
  public ResponseEntity<String> deleteSkillProgress(@PathVariable UUID skillProgressId) {
    skillProgressService.deleteSkillProgresses(List.of(skillProgressId));
    return ResponseEntity.ok("Skill progress successfully deleted");
  }

  @PreAuthorize("hasAuthority('declared-skill:delete:own')")
  @DeleteMapping()
  public ResponseEntity<String> deleteSkillProgresses(@RequestBody List<UUID> skillProgressIds) {
    skillProgressService.deleteSkillProgresses(skillProgressIds);
    return ResponseEntity.ok("Skill progresses successfully deleted");
  }
}
