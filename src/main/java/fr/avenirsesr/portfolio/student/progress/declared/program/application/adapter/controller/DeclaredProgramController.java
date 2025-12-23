package fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.controller;

import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.dto.AddDeclaredProgramDTO;
import fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.dto.DeclaredProgramDTO;
import fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.dto.DeclaredProgramViewDTO;
import fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.mapper.DeclaredProgramMapper;
import fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.mapper.DeclaredProgramViewMapper;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.DeclaredProgram;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.port.input.DeclaredProgramService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/me/declared/programs")
public class DeclaredProgramController {
  private final DeclaredProgramService declaredProgramService;

  @GetMapping
  public ResponseEntity<PagedResponse<DeclaredProgramDTO>> getDeclaredPrograms(
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize) {
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to get declared programs (page= {}, size= {})",
        pageCriteria.page(),
        pageCriteria.pageSize());
    PagedResult<DeclaredProgram> declaredProgramPagedResult =
        declaredProgramService.getDeclaredPrograms(pageCriteria);

    return ResponseEntity.ok(
        new PagedResponse<>(
            declaredProgramPagedResult.content().stream()
                .map(DeclaredProgramMapper::toDTO)
                .toList(),
            PageInfoDTO.fromDomain(declaredProgramPagedResult.pageInfo())));
  }

  @PostMapping
  public ResponseEntity<DeclaredProgramViewDTO> createDeclaredProgram(
      @Valid @RequestBody AddDeclaredProgramDTO addDeclaredProgramDTO) {
    DeclaredProgram declaredProgram =
        declaredProgramService.create(
            addDeclaredProgramDTO.title(),
            addDeclaredProgramDTO.description(),
            addDeclaredProgramDTO.organization(),
            addDeclaredProgramDTO.result(),
            addDeclaredProgramDTO.sourceOfInformation(),
            addDeclaredProgramDTO.link(),
            addDeclaredProgramDTO.startDate(),
            addDeclaredProgramDTO.endDate());
    return ResponseEntity.created(URI.create("/me/declared/programs/" + declaredProgram.getId()))
        .body(DeclaredProgramViewMapper.toDTO(declaredProgram));
  }

  @GetMapping("/{declaredProgramId}")
  public ResponseEntity<DeclaredProgramViewDTO> getDeclaredProgram(
      @PathVariable("declaredProgramId") UUID declaredProgramId) {
    DeclaredProgram declaredProgram = declaredProgramService.getById(declaredProgramId);
    return ResponseEntity.ok(DeclaredProgramViewMapper.toDTO(declaredProgram));
  }
}
