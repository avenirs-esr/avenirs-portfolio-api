package fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.controller;

import fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.dto.AddDeclaredProgramDTO;
import fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.dto.DeclaredProgramViewDTO;
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
        .body(DeclaredProgramViewMapper.toDto(declaredProgram));
  }

  @GetMapping("/{declaredProgramId}")
  public ResponseEntity<DeclaredProgramViewDTO> getDeclaredProgram(
      @PathVariable("declaredProgramId") UUID declaredProgramId) {
    DeclaredProgram declaredProgram = declaredProgramService.getById(declaredProgramId);
    return ResponseEntity.ok(DeclaredProgramViewMapper.toDto(declaredProgram));
  }
}
