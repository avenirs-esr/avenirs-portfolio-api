package fr.avenirsesr.portfolio.user.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.notification.domain.port.output.repository.NotificationRepository;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import fr.avenirsesr.portfolio.user.domain.port.input.StaffService;
import fr.avenirsesr.portfolio.user.domain.port.input.StudentService;
import fr.avenirsesr.portfolio.user.domain.port.input.UserService;
import fr.avenirsesr.portfolio.user.domain.port.output.client.ExternalUserClient;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserPrincipalRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.user.domain.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class UserServiceConfig {

  private final UserRepository userRepository;
  private final UserPrincipalRepository userPrincipalRepository;
  private final StaffService staffService;
  private final StudentService studentService;
  private final ExternalUserClient externalUserClient;
  private final LoggedInUserService loggedInUserService;
  private final NotificationRepository notificationRepository;

  @Bean
  public UserService userService() {
    return new TransactionalUserService(
        new UserServiceImpl(
            userRepository,
            userPrincipalRepository,
            staffService,
            studentService,
            externalUserClient,
            loggedInUserService,
            notificationRepository));
  }
}
