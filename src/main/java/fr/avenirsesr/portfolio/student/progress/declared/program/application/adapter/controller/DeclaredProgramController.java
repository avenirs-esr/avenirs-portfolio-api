package fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.controller;

import fr.avenirsesr.portfolio.student.progress.declared.program.application.adapter.dto.AddDeclaredProgramDTO;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.model.DeclaredProgram;
import fr.avenirsesr.portfolio.student.progress.declared.program.domain.port.input.DeclaredProgramService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/me/declared/programs")
public class DeclaredProgramController {
  private final DeclaredProgramService declaredProgramService;

  @PostMapping
  public ResponseEntity<String> createDeclaredProgram(
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
        .body("Declared program created successfully");
  }
}
