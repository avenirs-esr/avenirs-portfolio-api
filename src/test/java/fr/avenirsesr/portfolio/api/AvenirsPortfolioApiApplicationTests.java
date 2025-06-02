package fr.avenirsesr.portfolio.api;

import fr.avenirsesr.portfolio.api.infrastructure.adapter.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.mock;

@SpringBootTest
@ActiveProfiles("test")
class AvenirsPortfolioApiApplicationTests {

  @TestConfiguration
  static class TestConfig {
    @Bean
    UserJpaRepository userJpaRepository() {
      return mock(UserJpaRepository.class);
    }

    @Bean
    ProgramProgressJpaRepository programProgressJpaRepository() {
      return mock(ProgramProgressJpaRepository.class);
    }

    @Bean
    ProgramJpaRepository programJpaRepository() {
      return mock(ProgramJpaRepository.class);
    }

    @Bean
    AMSJpaRepository amsJpaRepository() {
      return mock(AMSJpaRepository.class);
    }

    @Bean
    ExternalUserJpaRepository externalUserJpaRepository() {
      return mock(ExternalUserJpaRepository.class);
    }

    @Bean
    InstitutionJpaRepository institutionJpaRepository() {
      return mock(InstitutionJpaRepository.class);
    }

    @Bean
    TrackJpaRepository trackJpaRepository() {
      return mock(TrackJpaRepository.class);
    }

    @Bean
    SkillJpaRepository skillJpaRepository() {
      return mock(SkillJpaRepository.class);
    }

    @Bean
    SkillLevelJpaRepository skillLevelJpaRepository() {
      return mock(SkillLevelJpaRepository.class);
    }
  }

  @Test
  void contextLoads() {}
}
