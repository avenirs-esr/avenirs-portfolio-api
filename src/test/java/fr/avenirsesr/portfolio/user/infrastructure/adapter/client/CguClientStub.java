package fr.avenirsesr.portfolio.user.infrastructure.adapter.client;

import fr.avenirsesr.portfolio.common.cgu.application.adapter.dto.CguDTO;
import fr.avenirsesr.portfolio.user.domain.port.output.client.CguClient;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.Setter;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
@Primary
public class CguClientStub implements CguClient {

  @Setter private CguDTO latest = defaultCgu();

  private static CguDTO defaultCgu() {
    return new CguDTO(
        UUID.fromString("0a5b2d11-6a19-4c3a-9c2e-2a1b3c4d5e6f"),
        1,
        Instant.parse("2026-01-01T00:00:00Z"),
        "<html><body>Conditions générales</body></html>");
  }

  public void reset() {
    latest = defaultCgu();
  }

  @Override
  public Optional<CguDTO> getLatest() {
    return Optional.ofNullable(latest);
  }
}
