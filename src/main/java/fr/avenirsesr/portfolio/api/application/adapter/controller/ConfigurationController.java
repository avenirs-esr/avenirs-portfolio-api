package fr.avenirsesr.portfolio.api.application.adapter.controller;

import fr.avenirsesr.portfolio.api.domain.model.TraceConfigurationInfo;
import fr.avenirsesr.portfolio.api.domain.port.input.ConfigurationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping
public class ConfigurationController {
  private final ConfigurationService configurationService;

  @GetMapping("/traces/config")
  public ResponseEntity<TraceConfigurationInfo> getTraceConfigInfo() {
    log.debug("Received request to get trace config");

    return ResponseEntity.ok(configurationService.getTraceConfiguration());
  }
}
