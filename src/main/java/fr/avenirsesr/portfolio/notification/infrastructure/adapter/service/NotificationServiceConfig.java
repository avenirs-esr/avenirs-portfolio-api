package fr.avenirsesr.portfolio.notification.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.notification.domain.port.input.NotificationService;
import fr.avenirsesr.portfolio.notification.domain.port.output.repository.NotificationRepository;
import fr.avenirsesr.portfolio.notification.domain.service.NotificationServiceImpl;
import fr.avenirsesr.portfolio.shared.domain.port.input.LoggedInUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class NotificationServiceConfig {
  private final NotificationRepository notificationRepository;
  private final LoggedInUserService loggedInUserService;

  @Bean
  public NotificationService notificationService() {
    return new NotificationServiceImpl(notificationRepository, loggedInUserService);
  }
}
