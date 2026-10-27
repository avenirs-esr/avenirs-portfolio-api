package fr.avenirsesr.portfolio.authorization.infrastructure.adapter.service;

import fr.avenirsesr.portfolio.authorization.domain.port.input.EntityAuthorizationService;
import fr.avenirsesr.portfolio.authorization.domain.port.input.UserAuthorizationQueryService;
import fr.avenirsesr.portfolio.authorization.domain.port.output.EntityAuthorizationResolver;
import fr.avenirsesr.portfolio.authorization.domain.port.output.UserEntityAuthorizationReader;
import fr.avenirsesr.portfolio.authorization.domain.service.EntityAuthorizationServiceImpl;
import fr.avenirsesr.portfolio.authorization.domain.service.UserAuthorizationQueryServiceImpl;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserPrincipalRepository;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EntityAuthorizationServiceConfig {
  @Bean
  public EntityAuthorizationService entityAuthorizationService(
      List<EntityAuthorizationResolver> resolvers) {
    return new EntityAuthorizationServiceImpl(resolvers);
  }

  @Bean
  public UserAuthorizationQueryService userAuthorizationQueryService(
      List<UserEntityAuthorizationReader> readers,
      UserPrincipalRepository userPrincipalRepository) {
    return new UserAuthorizationQueryServiceImpl(readers, userPrincipalRepository);
  }
}
