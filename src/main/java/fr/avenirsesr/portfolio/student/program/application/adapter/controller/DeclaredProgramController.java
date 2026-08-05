package fr.avenirsesr.portfolio.student.program.application.adapter.controller;

import fr.avenirsesr.portfolio.common.data.application.adapter.dto.PageInfoDTO;
import fr.avenirsesr.portfolio.common.data.application.adapter.response.PagedResponse;
import fr.avenirsesr.portfolio.common.data.domain.model.PageCriteria;
import fr.avenirsesr.portfolio.common.data.domain.model.PagedResult;
import fr.avenirsesr.portfolio.student.program.application.adapter.dto.DeclaredProgramDetailedDTO;
import fr.avenirsesr.portfolio.student.program.application.adapter.dto.DeclaredProgramRequestDTO;
import fr.avenirsesr.portfolio.student.program.application.adapter.dto.DeclaredProgramViewDTO;
import fr.avenirsesr.portfolio.student.program.application.adapter.mapper.DeclaredProgramDetailedMapper;
import fr.avenirsesr.portfolio.student.program.application.adapter.mapper.DeclaredProgramViewMapper;
import fr.avenirsesr.portfolio.student.program.domain.model.DeclaredProgram;
import fr.avenirsesr.portfolio.student.program.domain.port.input.DeclaredProgramService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
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
  private final DeclaredProgramViewMapper declaredProgramViewMapper;
  private final DeclaredProgramDetailedMapper declaredProgramDetailedMapper;

  @GetMapping
  public ResponseEntity<PagedResponse<DeclaredProgramViewDTO>> getDeclaredPrograms(
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer pageSize,
      @RequestParam(required = false) Boolean isValorized) {
    var pageCriteria = new PageCriteria(page, pageSize);
    log.debug(
        "Received request to get declared programs (page= {}, fileSize= {}, isValorized={})",
        pageCriteria.page(),
        pageCriteria.pageSize(),
        isValorized);
    PagedResult<DeclaredProgram> declaredProgramPagedResult =
        declaredProgramService.getDeclaredPrograms(pageCriteria, isValorized);

    return ResponseEntity.ok(
        new PagedResponse<>(
            declaredProgramPagedResult.content().stream()
                .map(declaredProgramViewMapper::toDTO)
                .toList(),
            PageInfoDTO.fromDomain(declaredProgramPagedResult.pageInfo())));
  }

  @PostMapping
  public ResponseEntity<DeclaredProgramViewDTO> createDeclaredProgram(
      @Valid @RequestBody DeclaredProgramRequestDTO declaredProgramRequestDTO) {
    DeclaredProgram declaredProgram =
        declaredProgramService.create(
            declaredProgramRequestDTO.title(),
            declaredProgramRequestDTO.description(),
            declaredProgramRequestDTO.organization(),
            declaredProgramRequestDTO.result(),
            declaredProgramRequestDTO.sourceOfInformation(),
            declaredProgramRequestDTO.startDate(),
            declaredProgramRequestDTO.endDate());
    return ResponseEntity.created(URI.create("/me/declared/programs/" + declaredProgram.getId()))
        .body(declaredProgramViewMapper.toDTO(declaredProgram));
  }

  @PutMapping("/{declaredProgramId}")
  public ResponseEntity<DeclaredProgramDetailedDTO> updateDeclaredProgram(
      @Valid @PathVariable("declaredProgramId") UUID declaredProgramId,
      @Valid @RequestBody DeclaredProgramRequestDTO declaredProgramRequestDTO) {
    DeclaredProgram declaredProgram =
        declaredProgramService.update(
            declaredProgramId,
            declaredProgramRequestDTO.title(),
            declaredProgramRequestDTO.description(),
            declaredProgramRequestDTO.organization(),
            declaredProgramRequestDTO.result(),
            declaredProgramRequestDTO.sourceOfInformation(),
            declaredProgramRequestDTO.startDate(),
            declaredProgramRequestDTO.endDate(),
            Boolean.TRUE.equals(declaredProgramRequestDTO.valorized()));
    return ResponseEntity.ok(declaredProgramDetailedMapper.toDTO(declaredProgram));
  }

  @GetMapping("/{declaredProgramId}")
  public ResponseEntity<DeclaredProgramDetailedDTO> getDeclaredProgram(
      @PathVariable("declaredProgramId") UUID declaredProgramId) {
    DeclaredProgram declaredProgram = declaredProgramService.getById(declaredProgramId);
    return ResponseEntity.ok(declaredProgramDetailedMapper.toDTO(declaredProgram));
  }

  @DeleteMapping
  public ResponseEntity<String> deleteDeclaredProgram(@RequestBody List<UUID> declaredProgramIds) {
    declaredProgramService.delete(declaredProgramIds);
    return ResponseEntity.ok("Declared programs deleted successfully.");
  }
}
