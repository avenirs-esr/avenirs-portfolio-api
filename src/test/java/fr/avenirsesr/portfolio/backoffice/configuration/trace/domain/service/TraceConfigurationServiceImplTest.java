package fr.avenirsesr.portfolio.backoffice.configuration.trace.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.model.Configuration;
import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.model.EConfigurationScope;
import fr.avenirsesr.portfolio.backoffice.configuration.shared.domain.port.output.repository.ConfigurationRepository;
import fr.avenirsesr.portfolio.backoffice.configuration.trace.domain.model.ETraceConfiguration;
import fr.avenirsesr.portfolio.backoffice.configuration.trace.domain.model.TraceConfiguration;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TraceConfigurationServiceImplTest {

  @Mock private ConfigurationRepository configurationRepository;

  @InjectMocks private TraceConfigurationServiceImpl service;

  private List<Configuration> mockConfigurations;

  @BeforeEach
  void setUp() {
    mockConfigurations =
        List.of(
            Configuration.create(
                UUID.randomUUID(),
                EConfigurationScope.TRACE,
                ETraceConfiguration.MAX_REMINING_DAYS,
                "30"),
            Configuration.create(
                UUID.randomUUID(),
                EConfigurationScope.TRACE,
                ETraceConfiguration.MAX_REMINING_DAYS_BEFORE_WARNING,
                "10"),
            Configuration.create(
                UUID.randomUUID(),
                EConfigurationScope.TRACE,
                ETraceConfiguration.MAX_REMINING_DAYS_BEFORE_CRITICAL,
                "5"));

    when(configurationRepository.inScope(EConfigurationScope.TRACE)).thenReturn(mockConfigurations);
  }

  @Test
  void shouldReturnTraceConfigurationFromRepository() {
    // When
    TraceConfiguration traceConfiguration = service.getTraceConfiguration();

    // Then
    assertNotNull(traceConfiguration);
    assertEquals(30, traceConfiguration.maxRemainingDays());
    assertEquals(10, traceConfiguration.maxRemainingDaysBeforeWarning());
    assertEquals(5, traceConfiguration.maxRemainingDaysBeforeCritical());
    verify(configurationRepository).inScope(EConfigurationScope.TRACE);
  }

  @Test
  void shouldThrowExceptionIfConfigurationMissing() {
    // Given repository missing CRITICAL key
    when(configurationRepository.inScope(EConfigurationScope.TRACE))
        .thenReturn(
            List.of(
                Configuration.create(
                    UUID.randomUUID(),
                    EConfigurationScope.TRACE,
                    ETraceConfiguration.MAX_REMINING_DAYS,
                    "30"),
                Configuration.create(
                    UUID.randomUUID(),
                    EConfigurationScope.TRACE,
                    ETraceConfiguration.MAX_REMINING_DAYS_BEFORE_WARNING,
                    "10")));

    // Then
    assertThrows(java.util.NoSuchElementException.class, () -> service.getTraceConfiguration());
  }

  @Test
  void shouldSaveNewTraceConfigurationValues() {
    // Given
    TraceConfiguration newConfig = new TraceConfiguration(60, 20, 8);

    // When
    service.postTraceConfiguration(newConfig);

    // Then
    verify(configurationRepository).saveAll(anyList());
  }

  @Test
  void shouldUpdateExistingTraceConfigurationValues() {
    // Given
    TraceConfiguration updatedConfig = new TraceConfiguration(40, 15, 7);

    // When
    service.postTraceConfiguration(updatedConfig);

    // Then
    verify(configurationRepository).saveAll(anyList());
  }
}
