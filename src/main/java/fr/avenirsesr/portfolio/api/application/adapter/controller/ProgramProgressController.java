package fr.avenirsesr.portfolio.api.application.adapter.controller;

import fr.avenirsesr.portfolio.api.application.adapter.dto.ProgramProgressDTO;
import fr.avenirsesr.portfolio.api.application.adapter.mapper.ProgramProgressMapper;
import fr.avenirsesr.portfolio.api.domain.port.input.ProgramProgressService;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/program-progress")
public class ProgramProgressController {
  private final ProgramProgressService programProgressService;

  public ProgramProgressController(ProgramProgressService programProgressService) {
    this.programProgressService = programProgressService;
  }

  @GetMapping("/skills/overview")
  public List<ProgramProgressDTO> getSkillsOverview() {
    UUID uuid = UUID.fromString("a690cd08-20a3-4c6f-a4c8-a1ef5c9eceb8");
    return programProgressService.getSkillsOverview(uuid).stream()
        .map(ProgramProgressMapper::fromDomainToDto)
        .toList();
  }
}
