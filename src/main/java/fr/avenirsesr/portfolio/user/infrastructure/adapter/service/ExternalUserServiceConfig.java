package fr.avenirsesr.portfolio.user.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.user.domain.port.input.ExternalUserService;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.ExternalUserRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.user.domain.service.ExternalUserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ExternalUserServiceConfig {
  private final UserRepository userRepository;
  private final ExternalUserRepository externalUserRepository;

  @Bean
  public ExternalUserService externalUserService() {
    return new ExternalUserServiceImpl(userRepository, externalUserRepository);
  }
}
