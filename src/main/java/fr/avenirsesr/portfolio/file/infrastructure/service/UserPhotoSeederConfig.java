package fr.avenirsesr.portfolio.file.infrastructure.service;

import fr.avenirsesr.portfolio.common.file.domain.port.output.service.FileStorageService;
import fr.avenirsesr.portfolio.common.file.infrastructure.adapter.service.FileStorageServiceMock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserPhotoSeederConfig {

  @Bean
  @Qualifier("seederFileStorageService")
  public FileStorageService seederFileStorageService() {
    return new FileStorageServiceMock();
  }
}
