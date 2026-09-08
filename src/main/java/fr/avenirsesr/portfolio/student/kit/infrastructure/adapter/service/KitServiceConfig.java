package fr.avenirsesr.portfolio.student.kit.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.student.kit.domain.port.input.KitService;
import fr.avenirsesr.portfolio.student.kit.domain.port.output.KitDownloadPort;
import fr.avenirsesr.portfolio.student.kit.domain.service.KitServiceImpl;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class KitServiceConfig {
  private final FileStorageService fileStorageService;
  private final List<KitDownloadPort> kitDownloadPorts;

  @Bean
  public KitService kitService() {
    return new KitServiceImpl(fileStorageService, kitDownloadPorts);
  }
}
