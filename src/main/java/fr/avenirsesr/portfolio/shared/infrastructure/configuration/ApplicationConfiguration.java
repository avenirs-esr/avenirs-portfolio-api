package fr.avenirsesr.portfolio.shared.infrastructure.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "customDateTimeProvider")
public class ApplicationConfiguration {}
