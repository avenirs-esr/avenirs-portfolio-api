package fr.avenirsesr.portfolio.api.infrastructure.configuration;

import fr.avenirsesr.portfolio.api.domain.port.input.ProfileService;
import fr.avenirsesr.portfolio.api.domain.port.output.UserRepository;
import fr.avenirsesr.portfolio.api.domain.service.ProfileServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    ProfileService userService(UserRepository userRepository) {
        return new ProfileServiceImpl(userRepository);
    }

}