package fr.avenirsesr.portfolio.user.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.file.domain.port.input.FileResourceService;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.user.domain.port.input.StaffService;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.StaffRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.user.domain.service.StaffServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
@RequiredArgsConstructor
public class StaffServiceConfig {
  private final StaffRepository staffRepository;
  private final UserRepository userRepository;

  @Bean
  public StaffService staffService(
      @Lazy LoggedInUserService loggedInUserService,
      @Lazy FileResourceService fileResourceService) {
    return new StaffServiceImpl(
        staffRepository, userRepository, loggedInUserService, fileResourceService);
  }
}
