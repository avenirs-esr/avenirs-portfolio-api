package fr.avenirsesr.portfolio.shared.infrastructure.service;

import fr.avenirsesr.portfolio.shared.domain.port.input.ClockService;
import fr.avenirsesr.portfolio.shared.domain.service.ClockServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClockServiceConfig {
  @Bean
  public ClockService clockService() {
    return new ClockServiceImpl();
  }
}
