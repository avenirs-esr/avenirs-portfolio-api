package fr.avenirsesr.portfolio.user.domain.port.input;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.user.domain.model.ExternalUser;
import fr.avenirsesr.portfolio.user.domain.model.enums.EExternalSource;
import java.util.UUID;

public interface ExternalUserService {
  ExternalUser importExternalUser(
      UUID userId,
      String firstName,
      String lastName,
      String email,
      EUserCategory category,
      String externalId,
      EExternalSource source);
}
