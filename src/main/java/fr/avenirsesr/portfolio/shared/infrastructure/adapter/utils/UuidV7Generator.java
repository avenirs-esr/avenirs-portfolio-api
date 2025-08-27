package fr.avenirsesr.portfolio.shared.infrastructure.adapter.utils;

import com.github.f4b6a3.uuid.UuidCreator;
import fr.avenirsesr.portfolio.shared.domain.port.output.utils.UuidGenerator;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UuidV7Generator implements UuidGenerator {

  @Override
  public UUID generate() {
    return UuidCreator.getTimeOrderedEpoch();
  }
}
