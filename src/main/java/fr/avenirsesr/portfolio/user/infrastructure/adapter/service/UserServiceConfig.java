package fr.avenirsesr.portfolio.user.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.file.domain.port.input.UserResourceService;
import fr.avenirsesr.portfolio.user.domain.port.input.UserService;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.user.domain.service.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserServiceConfig {

  private final UserRepository userRepository;
  private final UserResourceService userResourceService;

  public UserServiceConfig(UserRepository userRepository, UserResourceService userResourceService) {
    this.userRepository = userRepository;
    this.userResourceService = userResourceService;
  }

  @Bean
  public UserService userService() {
    return new UserServiceImpl(userRepository, userResourceService);
  }
}
