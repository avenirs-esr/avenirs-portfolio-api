package fr.avenirsesr.portfolio.user.domain.service;

import fr.avenirsesr.portfolio.common.data.domain.model.enums.EUserCategory;
import fr.avenirsesr.portfolio.common.error.domain.exception.UserNotFoundException;
import fr.avenirsesr.portfolio.user.domain.model.ExternalUser;
import fr.avenirsesr.portfolio.user.domain.model.enums.EExternalSource;
import fr.avenirsesr.portfolio.user.domain.port.input.ExternalUserService;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.ExternalUserRepository;
import fr.avenirsesr.portfolio.user.domain.port.output.repository.UserRepository;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class ExternalUserServiceImpl implements ExternalUserService {
  private final UserRepository userRepository;
  private final ExternalUserRepository externalUserRepository;

  @Override
  public ExternalUser importExternalUser(
      UUID userId,
      String firstName,
      String lastName,
      String email,
      EUserCategory category,
      String externalId,
      EExternalSource source) {
    var user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    var externalUser =
        ExternalUser.create(user, externalId, source, category, firstName, lastName, email);
    externalUserRepository.save(externalUser);
    return externalUser;
  }
}
