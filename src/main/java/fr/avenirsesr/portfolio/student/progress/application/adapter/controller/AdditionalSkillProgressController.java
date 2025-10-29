package fr.avenirsesr.portfolio.student.progress.application.adapter.controller;

import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.AdditionalSkillProgressDTO;
import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.AdditionalSkillProgressDetailsDTO;
import fr.avenirsesr.portfolio.student.progress.application.adapter.dto.AdditionalSkillProgressRequest;
import fr.avenirsesr.portfolio.student.progress.application.adapter.mapper.AdditionalSkillProgressMapper;
import fr.avenirsesr.portfolio.student.progress.application.adapter.request.AddAdditionalSkillDTO;
import fr.avenirsesr.portfolio.student.progress.domain.data.AdditionalSkillProgressDetails;
import fr.avenirsesr.portfolio.student.progress.domain.port.input.StudentProgressService;
import jakarta.validation.Valid;
import java.net.URI;
import java.security.Principal;
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

  @GetMapping()
  public ResponseEntity<PagedResponse<AdditionalSkillProgressDTO>> getAdditionalSkillsProgresses(
      Principal principal,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize) {
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to trace overview of user [{}] (page= {}, size= {})",
        principal.getName(),
        pageCriteria.page(),
        pageCriteria.pageSize());
    var result = studentProgressService.getAdditionalSkillsProgresses(pageCriteria);
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
    log.debug("Received request to create additional skill for student [{}]", principal.getName());
    var additionalSkillProgress =
        studentProgressService.createAdditionalSkillProgress(
            UUID.fromString(additionalSkill.getId()),
            additionalSkill.getType(),
            additionalSkill.getLevel(),
            additionalSkill.getDescription());
    return ResponseEntity.created(URI.create("/me/additional-skills/" + additionalSkill.getId()))
        .body(AdditionalSkillProgressMapper.toAdditionalSkillProgressDTO(additionalSkillProgress));
  }

  @PutMapping("/{additionalSkillProgressId}")
  public ResponseEntity<AdditionalSkillProgressDTO> updateAdditionalSkillProgress(
      Principal principal,
      @PathVariable UUID additionalSkillProgressId,
      @Valid @RequestBody AdditionalSkillProgressRequest additionalSkillProgressRequest) {
    log.debug(
        "Received request to update additional skill progress for student [{}]",
        principal.getName());
    var additionalSkillProgress =
        studentProgressService.updateAdditionalSkillProgress(
            additionalSkillProgressId,
            additionalSkillProgressRequest.level(),
            additionalSkillProgressRequest.description());
    return ResponseEntity.ok(
        AdditionalSkillProgressMapper.toAdditionalSkillProgressDTO(additionalSkillProgress));
  }

  @GetMapping("/{additionalSkillProgressId}")
  public ResponseEntity<AdditionalSkillProgressDetailsDTO> getAdditionalSkillProgressDetails(
      Principal principal, @PathVariable UUID additionalSkillProgressId) {
    log.debug(
        "Received request to detailed additional skill progress [{}] for student [{}]",
        additionalSkillProgressId,
        principal.getName());
    AdditionalSkillProgressDetails additionalSkillProgressDetails =
        studentProgressService.getAdditionalSkillProgressDetails(additionalSkillProgressId);

    return ResponseEntity.ok(
        AdditionalSkillProgressMapper.toAdditionalSkillProgressDetailsDTO(
            additionalSkillProgressDetails));
  }
}
