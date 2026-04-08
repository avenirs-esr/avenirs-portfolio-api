package fr.avenirsesr.portfolio.user.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.user.domain.port.input.StaffService;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StaffRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.user.domain.service.StaffServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class StaffServiceConfig {
  private final StaffRepository staffRepository;
  private final UserRepository userRepository;

  @Bean
  public StaffService staffService() {
    return new StaffServiceImpl(staffRepository, userRepository);
  }
}
