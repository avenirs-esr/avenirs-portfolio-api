package fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.application.controller;

import fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.application.dto.AdditionalSkillConfigurationDTO;
import fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.domain.model.AdditionalSkillConfiguration;
import fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.domain.model.AdditionalSkillLevel;
import fr.avenirsesr.portfolio.backoffice.configuration.additionalskill.domain.port.input.AdditionalSkillConfigurationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("back-office/config/additional-skills")
public class AdditionalSkillConfigController {
  private final AdditionalSkillConfigurationService service;

  @GetMapping
  public ResponseEntity<AdditionalSkillConfigurationDTO> getAdditionalSkillConfig() {
    log.debug("Received request to get additional-skills config");

    var config = service.getConfiguration();

    return ResponseEntity.ok(AdditionalSkillConfigurationDTO.from(config));
  }

  @PostMapping
  public ResponseEntity<Void> postAdditionalSkillConfig(
      @RequestBody AdditionalSkillConfigurationDTO config) {
    log.debug("Received request to post additional-skills config : {}", config);

    service.postConfiguration(
        new AdditionalSkillConfiguration(
            new AdditionalSkillLevel(config.BEGINNER().label(), config.BEGINNER().description()),
            new AdditionalSkillLevel(
                config.INTERMEDIATE().label(), config.INTERMEDIATE().description()),
            new AdditionalSkillLevel(config.COMPETENT().label(), config.COMPETENT().description()),
            new AdditionalSkillLevel(config.ADVANCED().label(), config.ADVANCED().description()),
            new AdditionalSkillLevel(config.EXPERT().label(), config.EXPERT().description())));

    return ResponseEntity.status(HttpStatus.ACCEPTED).build();
  }
}
