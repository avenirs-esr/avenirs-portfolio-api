package fr.avenirsesr.portfolio.api.infrastructure.configuration;

import fr.avenirsesr.portfolio.api.domain.port.input.RessourceService;
import fr.avenirsesr.portfolio.api.domain.port.input.UserService;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.RessourceRepository;
import fr.avenirsesr.portfolio.api.domain.port.output.repository.UserRepository;
import fr.avenirsesr.portfolio.api.domain.service.RessourceServiceImpl;
import fr.avenirsesr.portfolio.api.domain.service.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    UserService userService(UserRepository userRepository, RessourceRepository ressourceRepository) {
        return new UserServiceImpl(userRepository, ressourceRepository);
    }

    @Bean
    RessourceService ressourceService(RessourceRepository ressourceRepository) {
        return new RessourceServiceImpl(ressourceRepository);
    }

}